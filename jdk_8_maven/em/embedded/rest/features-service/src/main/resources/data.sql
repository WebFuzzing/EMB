insert into Product (id, name) values (1, 'ELEARNING_SITE');
insert into Feature (id, name,product_id) values (1, 'VIDEO_LESSONS',1);
insert into Feature (id, name,product_id) values (2, 'ONLINE_FORUM',1);
insert into Feature (id, name,product_id) values (3, 'CHAT',1);
insert into Feature (id, name,product_id) values (4, 'MAILING_LIST',1);
insert into Feature (id, name,product_id) values (5, 'COURSE_SELLING',1);
insert into Feature (id, name,product_id) values (6, 'PAYPAL_PAYMENT',1);
insert into Feature (id, name,product_id) values (7, 'CREDIT_CARD_PAYMENT',1);
insert into Feature (id, name,product_id) values (8, 'REDEEM_CODES',1);
insert into Feature (id, name,product_id) values (9, 'IN_TRIAL_PERIOD',1);

insert into Constraint_Requires (id, for_product_id, source_feature_name, required_feature_name ) values (1,1,'PAYPAL_PAYMENT','COURSE_SELLING');
insert into Constraint_Requires (id, for_product_id, source_feature_name, required_feature_name ) values (2,1,'CREDIT_CARD','COURSE_SELLING');
insert into Constraint_Excludes (id, for_product_id, source_feature_name, excluded_feature_name ) values (3,1,'IN_TRIAL_PERIOD','COURSE_SELLING');

insert into Product_Configuration (id, name,product_id, valid) values (1, 'UNIVERSITY_X',1, 1);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (1,1);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (1,2);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (1,3);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (1,9);



