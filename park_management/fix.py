#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, re, shutil, datetime
from pathlib import Path
from typing import List, Optional

PROJECT_ROOT = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
SRC = PROJECT_ROOT / "src" / "main" / "java"
POM = PROJECT_ROOT / "pom.xml"

# ---------- fs helpers ----------
def read(p: Path) -> str:
    return p.read_text(encoding="utf-8")

def write(p: Path, s: str):
    ts = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
    bak = p.with_suffix(p.suffix + f".auto.{ts}.bak")
    try:
        shutil.copy2(p, bak)
    except Exception:
        pass
    p.write_text(s, encoding="utf-8")
    print(f"[patch] {p}")

def ensure_import(java_src: str, fqcn: str) -> str:
    if re.search(rf'^\s*import\s+{re.escape(fqcn)}\s*;\s*$', java_src, flags=re.M):
        return java_src
    m = re.search(r'^\s*package\s+[^\n]+;\s*', java_src, flags=re.M)
    line = f"import {fqcn};\n"
    return (java_src[:m.end()] + "\n" + line + java_src[m.end():]) if m else (line + java_src)

def append_before_last_brace(java_src: str, block: str) -> str:
    idx = java_src.rfind('}')
    return (java_src[:idx] + "\n" + block + "\n" + java_src[idx:]) if idx != -1 else (java_src + "\n" + block + "\n")

def has_method(java_src: str, sig_regex: str) -> bool:
    return re.search(sig_regex, java_src) is not None

def class_name(java_src: str) -> Optional[str]:
    m = re.search(r'public\s+class\s+([A-Za-z_][A-Za-z0-9_]*)', java_src)
    return m.group(1) if m else None

def ensure_logger(java_src: str, cls: str) -> str:
    if re.search(r'Logger\s+log\s*=', java_src):
        return java_src
    java_src = ensure_import(java_src, "org.slf4j.Logger")
    java_src = ensure_import(java_src, "org.slf4j.LoggerFactory")
    m = re.search(rf'public\s+class\s+{re.escape(cls)}\b[^\{{]*\{{', java_src)
    return java_src if not m else (java_src[:m.end()] + f'\n    private static final Logger log = LoggerFactory.getLogger({cls}.class);\n' + java_src[m.end():])

# ---------- ErrorCode parsing ----------
def parse_error_codes() -> List[str]:
    p = SRC / "park/management/com/vn/exception/ErrorCode.java"
    if not p.exists(): return []
    src = read(p)
    m = re.search(r'enum\s+ErrorCode\s*\{(.*?)\}', src, flags=re.S)
    if not m: return []
    body = m.group(1)
    head = body.split(';', 1)[0]
    names = []
    for token in re.split(r'[, \n\r\t]+', head):
        token = token.strip()
        if re.fullmatch(r'[A-Z0-9_]+', token):
            names.append(token)
    return names

ERROR_CODES = parse_error_codes()

def pick_error_code(tokens: List[str]) -> Optional[str]:
    if not ERROR_CODES: return None
    best, score_best = None, -1
    for const in ERROR_CODES:
        score = 0
        cup = const.upper()
        for t in tokens:
            if t in cup: score += 2
            if t == "EXCEED" and ("EXCEED" in cup or "EXCEEDED" in cup): score += 1
            if t in ("INACTIVE","NOT_ACTIVE") and ("INACTIVE" in cup or "NOT_ACTIVE" in cup): score += 1
        if "NOT" in tokens and "FOUND" in tokens and "NOT_FOUND" in cup: score += 1
        if score > score_best:
            best, score_best = const, score
    return best

def code_expr_for(tokens: List[str]) -> str:
    picked = pick_error_code(tokens)
    return ("ErrorCode." + picked) if picked else "null"

# ---------- patch ErrorCode enum ----------
def patch_errorcode():
    path = SRC / "park/management/com/vn/exception/ErrorCode.java"
    if not path.exists():
        print("[skip] ErrorCode.java not found"); return
    src = read(path)
    # if constructor already present, ensure getters exist
    if re.search(r'ErrorCode\s*\(\s*String\s+\w+\s*,\s*HttpStatus\s+\w+\s*\)', src):
        need = False
        if "getDefaultMessage(" not in src:
            src = append_before_last_brace(src, "    public String getDefaultMessage() { return this.defaultMessage; }\n"); need = True
        if "getHttpStatus(" not in src:
            src = append_before_last_brace(src, "    public org.springframework.http.HttpStatus getHttpStatus() { return this.httpStatus; }\n"); need = True
        if need: write(path, src)
        else: print("[ok] ErrorCode has ctor/getters")
        return

    # add fields + ctor + getters (if constants have arguments)
    if re.search(r'\b[A-Z0-9_]+\s*\(', src):
        src = ensure_import(src, "org.springframework.http.HttpStatus")
        # ensure semicolon after constants; and inject fields
        if re.search(r'enum\s+ErrorCode\s*\{[^}]*?;', src, flags=re.S):
            src = re.sub(r'(enum\s+ErrorCode\s*\{[^}]*?;)',
                         r'\1\n    private final String defaultMessage;\n    private final HttpStatus httpStatus;\n',
                         src, count=1, flags=re.S)
        else:
            src = re.sub(r'(enum\s+ErrorCode\s*\{[^}]*?\})',
                         lambda m: m.group(0)[:-1] + ";\n    private final String defaultMessage;\n    private final HttpStatus httpStatus;\n}",
                         src, count=1, flags=re.S)
        ctor = """
    ErrorCode(String defaultMessage, HttpStatus httpStatus) {
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
"""
        getters = """
    public String getDefaultMessage() { return this.defaultMessage; }
    public HttpStatus getHttpStatus() { return this.httpStatus; }
"""
        src = append_before_last_brace(src, ctor)
        src = append_before_last_brace(src, getters)
        write(path, src)
    else:
        print("[warn] ErrorCode constants don’t have args; leaving as-is.")

# ---------- patch BaseException ----------
def patch_baseexception():
    path = SRC / "park/management/com/vn/exception/BaseException.java"
    if not path.exists(): print("[skip] BaseException.java not found"); return
    src = read(path)
    src = ensure_import(src, "park.management.com.vn.exception.ErrorCode")
    src = ensure_import(src, "org.springframework.http.HttpStatus")
    # field
    if "ErrorCode errorCode;" not in src:
        src = re.sub(r'(public\s+class\s+BaseException\s*[^{]*\{)', r'\1\n    private final ErrorCode errorCode;\n', src, count=1)
    # (String, ErrorCode)
    if not has_method(src, r'BaseException\s*\(\s*String\s+\w+\s*,\s*ErrorCode\s+\w+\s*\)'):
        src = append_before_last_brace(src, """
    public BaseException(String message, ErrorCode code) {
        super(message);
        this.errorCode = code;
    }
""")
    # (ErrorCode)
    if not has_method(src, r'BaseException\s*\(\s*ErrorCode\s+\w+\s*\)'):
        src = append_before_last_brace(src, """
    public BaseException(ErrorCode code) {
        this(code != null
                 ? (code.getDefaultMessage() != null ? code.getDefaultMessage() : code.name())
                 : "UNKNOWN",
             code);
    }
""")
    # getter + httpStatus()
    if "getErrorCode(" not in src:
        src = append_before_last_brace(src, "    public ErrorCode getErrorCode() { return this.errorCode; }\n")
    if not has_method(src, r'HttpStatus\s+httpStatus\s*\('):
        src = append_before_last_brace(src, "    public HttpStatus httpStatus() { return errorCode != null ? errorCode.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR; }\n")
    write(path, src)

# ---------- GlobalExceptionHandler logger ----------
def patch_global_exception_handler():
    path = SRC / "park/management/com/vn/exception/GlobalExceptionHandler.java"
    if not path.exists(): print("[skip] GlobalExceptionHandler.java not found"); return
    src = read(path)
    cls = class_name(src)
    if not cls: print("[warn] cannot detect class name for GlobalExceptionHandler"); return
    src2 = ensure_logger(src, cls)
    if src2 != src: write(path, src2)
    else: print("[ok] GlobalExceptionHandler already has a logger")

# ---------- Lombok in pom.xml ----------
def patch_pom_for_lombok():
    if not POM.exists(): print("[skip] pom.xml not found"); return
    xml = read(POM)
    changed = False
    if "org.projectlombok" not in xml:
        dep = """
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.32</version>
            <scope>provided</scope>
        </dependency>"""
        if "</dependencies>" in xml:
            xml = xml.replace("</dependencies>", dep + "\n    </dependencies>")
        else:
            xml = xml.replace("</project>", "  <dependencies>\n" + dep + "\n  </dependencies>\n</project>")
        changed = True

    if "<artifactId>maven-compiler-plugin</artifactId>" in xml and "annotationProcessorPaths" not in xml:
        xml = re.sub(
            r'(<artifactId>maven-compiler-plugin</artifactId>.*?<configuration>)(.*?)</configuration>',
            r'\1\2\n        <annotationProcessorPaths>\n'
            r'          <path>\n'
            r'            <groupId>org.projectlombok</groupId>\n'
            r'            <artifactId>lombok</artifactId>\n'
            r'            <version>1.18.32</version>\n'
            r'          </path>\n'
            r'        </annotationProcessorPaths>\n      </configuration>',
            xml, flags=re.S)
        changed = True
    elif "<artifactId>maven-compiler-plugin</artifactId>" not in xml:
        plugin = """
      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
        <configuration>
          <source>17</source>
          <target>17</target>
          <annotationProcessorPaths>
            <path>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <version>1.18.32</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
      </plugin>"""
        if "</plugins>" in xml:
            xml = xml.replace("</plugins>", plugin + "\n    </plugins>")
        elif "</build>" in xml:
            xml = xml.replace("</build>", "  <plugins>\n" + plugin + "\n  </plugins>\n</build>")
        else:
            xml = xml.replace("</project>", "  <build>\n    <plugins>\n" + plugin + "\n    </plugins>\n  </build>\n</project>")
        changed = True

    if changed: write(POM, xml)
    else: print("[ok] pom.xml already Lombok-ready")

# ---------- Add Lombok to specific classes ----------
LOMBOK_TARGETS = {
    "park/management/com/vn/entity/BranchReview.java":                   {"builder": False, "ctors": True},
    "park/management/com/vn/entity/BranchStaff.java":                    {"builder": False, "ctors": True},
    "park/management/com/vn/entity/BranchPromotion.java":                {"builder": False, "ctors": True},
    "park/management/com/vn/entity/BulkPricingRule.java":                {"builder": False, "ctors": True},
    "park/management/com/vn/entity/DailyTicketInventory.java":           {"builder": False, "ctors": True},
    "park/management/com/vn/entity/TicketType.java":                     {"builder": False, "ctors": True},
    "park/management/com/vn/entity/TransactionRecord.java":              {"builder": False, "ctors": True},
    "park/management/com/vn/entity/TicketOrder.java":                    {"builder": True,  "ctors": True},
    "park/management/com/vn/entity/TicketDetail.java":                   {"builder": True,  "ctors": True},
    "park/management/com/vn/model/request/BranchReviewRequest.java":     {"builder": False, "ctors": True},
    "park/management/com/vn/model/request/BranchStaffRequest.java":      {"builder": False, "ctors": True},
    "park/management/com/vn/model/request/ParkBranchRequest.java":       {"builder": False, "ctors": True},
    "park/management/com/vn/model/request/RoleRequest.java":             {"builder": False, "ctors": True},
    "park/management/com/vn/model/request/ShiftRequest.java":            {"builder": False, "ctors": True},
    "park/management/com/vn/model/request/StaffAssignmentRequest.java":  {"builder": False, "ctors": True},
    "park/management/com/vn/model/request/TicketRequest.java":           {"builder": False, "ctors": True},
    "park/management/com/vn/model/request/TransactionRecordRequest.java":{"builder": False, "ctors": True},
    "park/management/com/vn/model/request/NotificationRequest.java":     {"builder": False, "ctors": True},
    "park/management/com/vn/model/response/TicketDetailResponse.java":   {"builder": False, "ctors": True},
}

def add_lombok_annotations(java_src: str, cls: str, want_builder=False, want_ctors=False) -> str:
    if "@Getter" in java_src or "@Data" in java_src:
        return java_src
    imps = ["lombok.Getter", "lombok.Setter"]
    ann = "@Getter @Setter"
    if want_builder:
        imps.append("lombok.Builder"); ann += " @Builder"
    if want_ctors:
        imps += ["lombok.NoArgsConstructor", "lombok.AllArgsConstructor"]; ann += " @NoArgsConstructor @AllArgsConstructor"
    for fq in imps: java_src = ensure_import(java_src, fq)
    java_src = re.sub(rf'(public\s+class\s+{re.escape(cls)}\b)',
                      ann + "\n\\1", java_src, count=1)
    return java_src

def patch_lombok_targets():
    for rel, opts in LOMBOK_TARGETS.items():
        p = SRC / rel
        if not p.exists(): continue
        src = read(p)
        cls = class_name(src)
        if not cls: continue
        new_src = add_lombok_annotations(src, cls, want_builder=opts["builder"], want_ctors=opts["ctors"])
        if new_src != src: write(p, new_src)

# ---------- Exception ctors used by services ----------
EXTRA_EXCEPTION_CTORS = {
    "DailyTicketInventoryExceedException": [
        ([("java.time.LocalDate","date")], ["DAILY","TICKET","INVENTORY","EXCEED"])
    ],
    "PromotionNotActiveException": [
        ([("Long","promotionId")], ["PROMOTION","NOT","ACTIVE","INACTIVE"])
    ],
    "PromotionExpiredException": [
        ([("Long","promotionId")], ["PROMOTION","EXPIRED"])
    ],
    "InvalidPromotionBranchException": [
        ([("Long","promotionId")], ["PROMOTION","BRANCH","INVALID"])
    ],
    "PromotionNotFoundException": [
        ([("Long","id")], ["PROMOTION","NOT","FOUND"])
    ],
    "TicketNotFoundException": [
        ([("Long","id")], ["TICKET","NOT","FOUND"])
    ],
    "TicketStatusInvalidException": [
        ([("Long","ticketId")], ["TICKET","STATUS","INVALID"])
    ],
    "TicketTypeNotFoundException": [
        ([("Long","id")], ["TICKET","TYPE","NOT","FOUND"])
    ],
    "ParkBranchNotFoundException": [
        ([("Long","id")], ["PARK","BRANCH","NOT","FOUND"])
    ],
    "PriceNotFoundException": [
        ([("Long","id")], ["PRICE","NOT","FOUND"])
    ],
    "UserNotFoundException": [
        ([("Long","id")], ["USER","NOT","FOUND"])
    ],
    "UserPasswordInvalidException": [
        ([("String","username")], ["USER","PASSWORD","INVALID"])
    ],
    "DailyTicketInventoryNotFoundException": [
        ([("Long","branchId"), ("java.time.LocalDate","date")], ["DAILY","TICKET","INVENTORY","NOT","FOUND"])
    ],
}

def ensure_exception_ctor(java_src: str, cls: str, params, tokens):
    # imports
    for (t, _) in params:
        if t == "java.time.LocalDate":
            java_src = ensure_import(java_src, "java.time.LocalDate")
    java_src = ensure_import(java_src, "park.management.com.vn.exception.ErrorCode")

    # message builder
    nice = cls.replace("Exception","")
    nice = (nice.replace("NotFound"," not found")
                 .replace("Invalid"," invalid")
                 .replace("Expired"," expired")
                 .replace("NotActive"," not active"))
    parts = []
    for (_, n) in params:
        parts.append(f'{n}=" + {n} + "')
    msg = '"' + nice + ': ' + ", ".join(parts) + '"'

    code_expr = code_expr_for(tokens)  # choose real constant, else null

    sig = ", ".join([f"{t.split('.')[-1]} {n}" if '.' in t else f"{t} {n}" for (t, n) in params])
    if re.search(rf'public\s+{re.escape(cls)}\s*\(\s*{re.escape(sig)}\s*\)\s*\{{', java_src):
        return java_src
    block = f"""
    public {cls}({sig}) {{
        super({msg}, {code_expr});
    }}
"""
    return append_before_last_brace(java_src, block)

def patch_exception_subclasses():
    exc_root = SRC / "park/management/com/vn/exception"
    for root, _, files in os.walk(exc_root):
        for fn in files:
            if not fn.endswith(".java"): continue
            cls = fn[:-5]
            if cls in EXTRA_EXCEPTION_CTORS:
                p = Path(root) / fn
                src = read(p)
                for (params, tokens) in EXTRA_EXCEPTION_CTORS[cls]:
                    src = ensure_exception_ctor(src, cls, params, tokens)
                write(p, src)

# ---------- Small domain-specific aliases ----------
def patch_branch_promotion_aliases():
    p = SRC / "park/management/com/vn/entity/BranchPromotion.java"
    if not p.exists(): return
    src = read(p)
    src = ensure_import(src, "java.math.BigDecimal")
    src = ensure_import(src, "java.time.LocalDate")
    src = ensure_import(src, "park.management.com.vn.constant.DiscountType")
    # add getIsActive() if missing (service calls this name)
    if " getIsActive(" not in src:
        src = append_before_last_brace(src, """
    public Boolean getIsActive() { return this.isActive; }
""")
    # add getDiscountValue() if field exists but no getter (Lombok should handle; alias is safe)
    if " getDiscountValue(" not in src and " discountValue" in src:
        src = append_before_last_brace(src, "    public BigDecimal getDiscountValue() { return this.discountValue; }\n")
    # add getValidFrom/Until aliases if fields exist and getters not found (Lombok should create; just in case)
    if " getValidFrom(" not in src and " validFrom" in src:
        src = append_before_last_brace(src, "    public LocalDate getValidFrom() { return this.validFrom; }\n")
    if " getValidUntil(" not in src and " validUntil" in src:
        src = append_before_last_brace(src, "    public LocalDate getValidUntil() { return this.validUntil; }\n")
    write(p, src)

def patch_bulk_pricing_rule_alias():
    p = SRC / "park/management/com/vn/entity/BulkPricingRule.java"
    if not p.exists(): return
    src = read(p)
    src = ensure_import(src, "java.math.BigDecimal")
    if " getDiscountPercent(" not in src and " discountPercent" in src:
        src = append_before_last_brace(src, "    public BigDecimal getDiscountPercent() { return this.discountPercent; }\n")
        write(p, src)

# ---------- main ----------
def main():
    print(f"[+] Project root: {PROJECT_ROOT}")
    patch_errorcode()
    patch_baseexception()
    patch_global_exception_handler()
    patch_pom_for_lombok()
    patch_lombok_targets()
    patch_exception_subclasses()
    patch_branch_promotion_aliases()
    patch_bulk_pricing_rule_alias()
    print("[done] Now run: mvn -DskipTests clean compile")

if __name__ == "__main__":
    main()
