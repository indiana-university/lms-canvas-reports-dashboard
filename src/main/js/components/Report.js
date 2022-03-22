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

  return (
        <li className="dataView">
            <div className="rvt-box rvt-box--card boxCardOverride">
                <div className="rvt-box__image">
                <input type="image" src={reportModel.thumbnail} className="img-button rvt-button rvt-button--plain" data-modal-trigger="modal-card-popup"
                     onClick={openModal.bind(this, reportModel.id, props.openModalMethod)} alt={'View preview of ' + reportModel.title} />

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

function openModal(reportId, openModalMethod, event) {
    openModalMethod(reportId)
}

export default Report
