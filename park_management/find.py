#!/usr/bin/env python3
"""
find_walletrepo_dupes.py
Search the project for *top-level* declarations of WalletRepository.

Usage:
  python find_walletrepo_dupes.py --root /path/to/repo
"""

import argparse
import re
from pathlib import Path
import sys

DECL_TMPL = r'\b(?:public|protected|private|abstract|final|static|sealed|non-sealed|\s)*' \
            r'(class|interface|enum|record)\s+{name}\b'

PKG_RE = re.compile(r'^\s*package\s+([A-Za-z0-9_.]+)\s*;', re.M)


def strip_comments_and_strings(src: str) -> str:
    # Remove /* ... */ comments
    src = re.sub(r'/\*.*?\*/', '', src, flags=re.S)
    # Remove // ... comments
    src = re.sub(r'//.*', '', src)
    # Mask string & char literals
    src = re.sub(r'"(?:\\.|[^"\\])*"', '""', src)
    src = re.sub(r"'(?:\\.|[^'\\])'", "''", src)
    return src


def brace_depth_at(text: str, idx: int) -> int:
    # Simple brace depth (best-effort) up to idx
    depth = 0
    for ch in text[:idx]:
        if ch == '{':
            depth += 1
        elif ch == '}':
            depth -= 1
    return depth


def find_declarations(java_path: Path, target_name: str):
    try:
        raw = java_path.read_text(encoding='utf-8', errors='ignore')
    except Exception:
        return []

    cleaned = strip_comments_and_strings(raw)
    decl_re = re.compile(DECL_TMPL.format(name=re.escape(target_name)))
    pkg_match = PKG_RE.search(cleaned)
    pkg = pkg_match.group(1) if pkg_match else None

    results = []
    for m in decl_re.finditer(cleaned):
        # Only count top-level declarations (brace depth == 0)
        if brace_depth_at(cleaned, m.start()) == 0:
            line = raw.count('\n', 0, m.start()) + 1
            kind = re.search(r'(class|interface|enum|record)\s+' + re.escape(target_name),
                             m.group(0)).group(1)
            results.append({
                'path': str(java_path),
                'line': line,
                'kind': kind,
                'package': pkg,
            })
    return results


def should_skip(path: Path) -> bool:
    skip_dirs = {'.git', 'target', 'build', 'out', '.idea', '.vscode', 'node_modules'}
    parts = set(p.name for p in path.parts)
    return bool(parts & skip_dirs)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--root', default='.', help='Project root (default: current directory)')
    ap.add_argument('--name', default='WalletRepository', help='Type name to search (default: WalletRepository)')
    args = ap.parse_args()

    root = Path(args.root).resolve()
    matches = []

    for p in root.rglob('*.java'):
        if any(part in {'.git', 'target', 'build', 'out', '.idea', '.vscode', 'node_modules'} for part in p.parts):
            continue
        matches.extend(find_declarations(p, args.name))

    # Report
    if not matches:
        print(f'No declarations of {args.name} found.')
        sys.exit(0)

    print(f'Found {len(matches)} declaration(s) of {args.name}:\n')
    by_file = {}
    for m in matches:
        by_file.setdefault(m["path"], []).append(m)

    for path, items in by_file.items():
        multi = '  [MULTIPLE IN SAME FILE]' if len(items) > 1 else ''
        print(f'{path}{multi}')
        for m in items:
            fqcn = f'{m["package"]}.{args.name}' if m["package"] else args.name
            print(f'  - line {m["line"]}: {m["kind"]} {args.name}  (package: {m["package"] or "NONE"})  FQCN: {fqcn}')
        print()

    # Exit non-zero if duplicates exist (more than one total, or multiple in a single file)
    duplicate_total = len(matches) > 1
    duplicate_in_file = any(len(v) > 1 for v in by_file.values())
    if duplicate_total or duplicate_in_file:
        print('❌ Duplicate declarations detected. Keep exactly one top-level declaration of WalletRepository.')
        sys.exit(1)
    else:
        print('✅ Exactly one top-level declaration found.')
        sys.exit(0)


if __name__ == '__main__':
    main()
