insert into LMS_REPORTS (id, title, description, contact, url, createdOn, modifiedOn, sis_courses_only, new_window)
    values ('1', 'Report 1', 'This is the first report', 'qwerty', 'http://google.com', '2019-05-09 15:15:15', '2019-05-09 15:15:15', true, false);

insert into LMS_REPORT_ROLES (report_id, allowed_role) values ('1', 'TeacherEnrollment');

insert into LMS_REPORTS (id, title, description, contact, url, createdOn, modifiedOn, sis_courses_only, new_window)
    values ('2', 'Report 2', 'This is the second report', 'asdf', 'http://iu.edu', '2019-05-09 15:15:15', '2019-05-09 15:15:15', true, true);

insert into LMS_REPORT_ROLES (report_id, allowed_role) values ('2', 'TeacherEnrollment');
insert into LMS_REPORT_ROLES (report_id, allowed_role) values ('2', 'StudentEnrollment');

insert into LMS_REPORTS (id, title, description, contact, url, createdOn, modifiedOn, sis_courses_only, new_window)
    values ('3', 'Report 3', 'This is the third report', 'asdf', 'http://iupui.edu', '2019-05-09 15:15:15', '2019-05-09 15:15:15', false, false);

insert into LMS_REPORT_ROLES (report_id, allowed_role) values ('3', 'DesignerEnrollment');