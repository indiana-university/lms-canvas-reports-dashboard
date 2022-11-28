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
import React from 'react'

const Report = (props) => {
  var reportModel = props.report;

  var kbLink = '';

  if (reportModel.kbUrl != null) {
      let learnMoreSRText = ' about ' + reportModel.title + ' (Opens in new window)';
      kbLink = <a href={reportModel.kbUrl} target='_blank'>Learn More<span className="sr-only">{learnMoreSRText}</span>
                  <svg aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 16 16" className="external-link">
                    <g fill="currentColor">
                        <path d="M10.75,15H2.25A1.25,1.25,0,0,1,1,13.75V5.25A1.25,1.25,0,0,1,2.25,4H6A1,1,0,0,1,6,6H3v7h7V10a1,1,0,0,1,2,0v3.75A1.25,1.25,0,0,1,10.75,15Z"/>
                        <path d="M14.71,1.29A1,1,0,0,0,13.87,1l-.12,0H9A1,1,0,0,0,9,3h2.59L7.29,7.29A1,1,0,1,0,8.71,8.71L13,4.41V7a1,1,0,0,0,2,0V2.25a1.17,1.17,0,0,0,0-.12A1,1,0,0,0,14.71,1.29Z"/>
                    </g>
                  </svg>
               </a>;
  }

  var reportTarget = '';
  if (reportModel.newWindow) {
    reportTarget = "_blank";
  }

  let openReportSRText = ', ' + reportModel.title + ' (Opens in new window)'

  let modalTriggerId = "report-preview-" + reportModel.id;

  return (
        <li className="dataView">
            <div className="rvt-box rvt-box--card boxCardOverride">
                <div className="rvt-box__image">
                <input id={modalTriggerId} type="image" src={reportModel.thumbnail} className="img-button rvt-button rvt-button--plain popupTrigger" data-modal-trigger="modal-card-popup"
                     onClick={openModal.bind(this, reportModel.id, props.openModalMethod, modalTriggerId)} alt={'View preview of ' + reportModel.title} />

                </div>
                <div className="rvt-box__body">
                    <h2 className="rvt-ts-18 rvt-text-bold">{reportModel.title}</h2>
                    <p className="rvt-m-top-xxs">{reportModel.description} {kbLink}</p>
                    <a href={reportModel.url} className="rvt-link-bold" target={reportTarget}>Open Report
                        <span className="sr-only">{openReportSRText}</span>
                        <svg aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 16 16" className="external-link">
                            <g fill="currentColor">
                                <path d="M10.75,15H2.25A1.25,1.25,0,0,1,1,13.75V5.25A1.25,1.25,0,0,1,2.25,4H6A1,1,0,0,1,6,6H3v7h7V10a1,1,0,0,1,2,0v3.75A1.25,1.25,0,0,1,10.75,15Z"/>
                                <path d="M14.71,1.29A1,1,0,0,0,13.87,1l-.12,0H9A1,1,0,0,0,9,3h2.59L7.29,7.29A1,1,0,1,0,8.71,8.71L13,4.41V7a1,1,0,0,0,2,0V2.25a1.17,1.17,0,0,0,0-.12A1,1,0,0,0,14.71,1.29Z"/>
                            </g>
                        </svg>
                    </a>
                </div>
            </div>
        </li>
      );
}

function openModal(reportId, openModalMethod, triggerId, event) {
    // Change all of the triggers back to the default modal name
    // We change it temporarily on modal close to refocus on the correct report
    $(".popupTrigger").attr("data-modal-trigger", "modal-card-popup");

    openModalMethod(reportId, triggerId)
}

export default Report
