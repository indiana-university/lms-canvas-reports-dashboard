package edu.iu.uits.lms.reports.repository;

import edu.iu.uits.lms.reports.model.ReportListing;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ReportListingRepository extends PagingAndSortingRepository<ReportListing, Long> {

   /**
    * Return all reports that have a role that matches something in the passed in list.
    * @param roles
    * @return
    */
   List<ReportListing> findDistinctByAllowedRolesInOrderByTitleAsc(List<String> roles);

}
