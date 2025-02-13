package edu.iu.uits.lms.reports.services;

/*-
 * #%L
 * reports
 * %%
 * Copyright (C) 2015 - 2024 Indiana University
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

import edu.iu.uits.lms.reports.repository.LdapPersonRepository;
import edu.iu.uits.lms.reports.service.GroupService;
import edu.iu.uits.lms.reports.service.LdapGroupService;
import edu.iu.uits.lms.reports.service.NoOpGroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.NestedTestConfiguration;

import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.INHERIT;

@NestedTestConfiguration(INHERIT)
@Slf4j
public class GroupServiceTest {

    @Nested
    @SpringBootTest(classes = {NoOpGroupService.class, LdapGroupService.class})
    class NoOp {

        @Autowired
        private GroupService groupService;

        @Test
        void testNoOpGroupService() {
            Assertions.assertInstanceOf(NoOpGroupService.class, groupService);
        }
    }

    @Nested
    @SpringBootTest(classes = {NoOpGroupService.class, LdapGroupService.class})
    @ActiveProfiles("ldap")
    class Ldap {

        @Autowired
        private GroupService groupService;

        @MockBean
        private LdapPersonRepository ldapPersonRepository;

        @Test
        void testLdapGroupService() {
            Assertions.assertInstanceOf(LdapGroupService.class, groupService);
        }
    }

}
