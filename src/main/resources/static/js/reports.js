/*-
 * #%L
 * reports
 * %%
 * Copyright (C) 2015 - 2022 Indiana University
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Indiana University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
var reports = reports || {};

reports.applyAccessibilityOverrides = function() {
    // Add alternative labels to better describe the page links in the pager
    reports.pagerLabels();
    
    // add role=button to the anchor tags styled as buttons
    reports.addButtonRole();
    
    // add more descriptive labels to the form elements with implicit labels
    reports.addDescriptiveLabels();
    
    // There are several actions that the user may take that will wipe out our customizations for the pager. These
    // should re-add our customizations for the various actions.
    
    // If the user updates the number of entries per page, need to refresh the pager values
    $('select[name=reportTable_length]').change(function() {
    	reports.refreshPager();
    });
    
    // The pager is also wiped out if the table is sorted via click
    $('th.sorting, th.sorting_desc, th.sorting_asc').click(function() {
    	reports.refreshPager();
    });
    
    // or if the table is sorted via the keyboard
    $('th.sorting, th.sorting_desc, th.sorting_asc').keypress(function(e) {
    	if (e.which == 13) {
    		reports.refreshPager();
    	}
    });
    
    // or if search text is typed
   // set a delay so it doesn't refresh until the user stops typing
    var globalTimeout = null;  
    $('#reportTable_filter').find('input[type=search]').keyup(function() {
    	if (globalTimeout != null) {
    		clearTimeout(globalTimeout);
    	}
    	
    	globalTimeout = setTimeout(function() {
    		globalTimeout = null;  
    		reports.refreshPager();
    	}, 200);  
    });
}


reports.refreshPager = function() {
	reports.pagerLabels();
	$('a.paginate_button').attr('role', 'button'); // add the button role back in
}

reports.pagerLabels = function() { 
	
	// Add aria-labels to the page numbers in the pager since DataTables does not let us customize this
	$('a.paginate_button').each(function() {
		// Don't include the previous and next buttons
		if (!$(this).hasClass('previous') && !$(this).hasClass('next')) {	
			var pageNum = $(this).text();
			var text = 'Go to page ' + pageNum;
	        $(this).attr('aria-label', text);
		}   
    });
	
	// Also need to re-label page numbers if the pager link is clicked
	$('a.paginate_button').click(function() {
		// only refresh if they click an active link
    	if (!$(this).hasClass('disabled') && !$(this).hasClass('current')) {
    		reports.refreshPager();
    	}
    });
}

reports.addDescriptiveLabels = function () {
	$('#reportTable_filter').find('input[type=search]').attr('aria-describedby','searchText');
	//$('#reportTable_length').find('select[name=reportTable_length]').attr('aria-describedby','numEntriesText');
}

reports.addButtonRole = function() {
	$('a.dt-button').attr('role', 'button');
	$('a.paginate_button').attr('role', 'button');
}
