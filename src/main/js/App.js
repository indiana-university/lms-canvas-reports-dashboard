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
import styled from 'styled-components'
import axios from 'axios'

import Reports from 'components/Reports'
import ReportModal from 'components/ReportModal'
import Loading from 'components/Loading'
import {CircleArrow as ScrollUpButton} from 'react-scroll-up-button';

class App extends React.Component {
  /**
    * Initialization stuff
    */
  constructor() {
    super()

    // Set the x-auth-token head for all requests
    // The customId value got injected in to the react.html file and is a global variable
    axios.defaults.headers.common['X-Auth-Token'] = customId;

    this.state = {
        reports: [],
        emptyReport: { title: "", imageData: ""},
        loading: true
    }
  }

  /**
   * Call off to the REST endpoints to load data
   * Also set a listener for cleaning up the modal after close
   */
  componentDidMount(){
  var self = this;
    axios.all([getReports()])
        .then(axios.spread(function (reports) {
            self.setState({
                reports: reports.data,
                loading: false
            });
        }))
        .catch(error => {
            alert(error);
        });

    // clear the data on modal close
    document.addEventListener('modalClose', event => {
        if (event.detail.name() === 'modal-card-popup') {
            this.setState({modalReport: this.state.emptyReport})

            // There is only one modal, but rivet expects a unique modal for each
            // report. To return focus to the correct report when the modal
            // is closed, we need to set the report's data-modal-trigger attribute
            // to a unique value and manually focus the modal. We will re-set to the
            // generic data-modal-trigger the next time a report is clicked
            var trigger = document.getElementById(this.state.modalTrigger)
            trigger.setAttribute("data-modal-trigger", "modal-card-popup-temp")
            Modal.focusTrigger("modal-card-popup-temp")
        }

    }, false);
  }

  /**
   * Render
   */
  render() {
    return (
        <div>
            <div className="rvt-container" id="main-container" role="main">
                <h1 className="rvt-ts-36 rvt-p-top-sm">Reports and Dashboards</h1>
                <Loading loading={this.state.loading} />
                <Reports loading={this.state.loading} reports={this.state.reports}
                         openModalMethod={this.imageClickOpenModal.bind(this)} />
            </div>
            <ReportModal modalReport={this.state.modalReport} />
            <ScrollUpButton />
        </div>
    );
  }

  /**
   * Open the report modal
   */
  imageClickOpenModal(reportId, triggerId) {
    //Find just the single report
    let filteredReports = this.state.reports.filter((reportModel) => {
        return (reportModel.id == reportId)
    });

    var theReport = filteredReports[0];

    this.setState({modalReport: theReport, modalTrigger: triggerId})
  }
}

  function getReports() {
    return axios.get(`/app/rest/${window.config.courseid}/byRoles`);
  }

export default App
