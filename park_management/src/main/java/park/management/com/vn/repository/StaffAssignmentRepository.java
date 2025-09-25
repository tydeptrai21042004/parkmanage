package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import park.management.com.vn.entity.StaffAssignment;

import java.time.LocalDate;
import java.util.List;

public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {

    // still available if used elsewhere
    List<StaffAssignment> findByStaff_Id(Long staffId);

    // user’s shifts in a given date range (month boundaries calculated in service)
    @Query("""
      select sa from StaffAssignment sa
        join sa.staff s
        join s.userEntity u
       where u.id = :userId
         and sa.assignedDate between :start and :end
    """)
    List<StaffAssignment> findAllOfUserInRange(@Param("userId") Long userId,
                                               @Param("start") LocalDate start,
                                               @Param("end") LocalDate end);

    // all staff shifts of a branch in a given date range
    @Query("""
      select sa from StaffAssignment sa
        join sa.staff s
        join s.parkBranch b
       where b.id = :branchId
         and sa.assignedDate between :start and :end
    """)
    List<StaffAssignment> findAllOfBranchInRange(@Param("branchId") Long branchId,
                                                 @Param("start") LocalDate start,
                                                 @Param("end") LocalDate end);
}
