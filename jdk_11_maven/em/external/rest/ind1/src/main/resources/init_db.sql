
/* Values below is also represented in the object SeededEntriesController */
INSERT INTO contact (id, email, name, phone) VALUES ('32c42ef9-cc97-40bf-9590-66ff7a84ae04', 'test@eksempel.no', 'Test Testersen', '99887766');
INSERT INTO account_manager (id, name) VALUES ('01234567-9ABC-DEF0-1234-56789ABCDEF0', 'SelgerBritt');
INSERT INTO account_manager (id, name) VALUES ('01234567-9ABC-DEF0-1234-000000000012', 'SelgerBorgar');

INSERT INTO subscription_status (id, name) values ('CUSTOMER', 'Kunde');
INSERT INTO subscription_status (id, name) values ('TRIAL', 'Prøveabonnement');

INSERT INTO subscription_package VALUES ('19fa3abc-8dae-11e9-bc42-526af7764f64', 'foo-premium', 'foo premium');
INSERT INTO subscription_package VALUES ('fb0f2e57-cd1c-483f-ad3e-762e96ac9468', 'foo-basis', 'foo basis');

INSERT INTO market_category VALUES ('a88e1b4e-6b75-44fa-bcfa-643082160780', 'advokatfirma', 'Advokatfirma');
INSERT INTO market_category VALUES ('678527f7-d6b2-43d7-a0f0-f0c3703c08be', 'utdanning', 'Utdanning');

INSERT INTO customer (id, customer_number, name, signup, contact_id, ip_e_username, ip_e_password, subscription_status, account_manager, potential_users, subscription_package_id, market_category_id)
VALUES ('8374627f-a31f-43eb-ad6c-022f367e3c18', 'test-1000', 'foo Test Kunde AS', 'AA3AB', '32c42ef9-cc97-40bf-9590-66ff7a84ae04',
        'db9dcc1804ec6d91c73323c70a2841b1245322c35434ba03d4796fef0b7e1927', '966c879cec492058da3b711cce00702bced015f5a4df16c8d13d6c0ceceea772',
        'CUSTOMER', '01234567-9ABC-DEF0-1234-56789ABCDEF0', '100', '19fa3abc-8dae-11e9-bc42-526af7764f64', 'a88e1b4e-6b75-44fa-bcfa-643082160780');

INSERT INTO product (id, product_number, name, enabled, role_id, type) VALUES ('0d97d518-be2e-4da0-83a1-a0b895269097', '3002', 'Lovkommentar', true, 'd2cb8350-ca87-4756-957d-a1cc1e57d523', 'Hovedprodukt');
INSERT INTO product (id, product_number, name, enabled, role_id, type) VALUES ('0d97d518-be2e-4da0-83a1-a0b895269098', '3000', 'Backlist', true, 'd2cb8350-ca87-4756-957d-a1cc1e57d524', 'Hovedprodukt');
INSERT INTO product (id, product_number, name, enabled, role_id, type) VALUES ('0d97d518-be2e-4da0-83a1-a0b895269099', '3001', 'Frontlist', true, 'd2cb8350-ca87-4756-957d-a1cc1e57d525', 'Hovedprodukt');

INSERT INTO ip (id, ipv4address, description, customer_id) VALUES ('aa74627f-a31f-43eb-ad6c-022f367e3caa', '192.168.150.234', 'Test address for test-1000', '8374627f-a31f-43eb-ad6c-022f367e3c18');

INSERT INTO signup_token VALUES (CAST('360a6590-176d-4060-bb40-b0cff5f3e426' as UUID),
                                 'eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlF6a3dNRUkxUWtSR09UWTNRak5DTlVRNE9EVkRNRVUxUTBGRlFUZERNREJHTmtRd09EZ3dPQSJ9.eyJodHRwOi8vanVyaWRpa2EvY2xhaW1zL3Blcm1pc3Npb25zIjpbXSwiaHR0cDovL2p1cmlkaWthL2NsYWltcy9jdXN0b21lcklkIjoidGVzdC0xMDAwIiwiaXNzIjoiaHR0cHM6Ly9qdXJpZGlrYS5ldS5hdXRoMC5jb20vIiwic3ViIjoiYXV0aDB8NWE2NzY0NDNkZjUzMGY1ZjcyYWU0YmFmIiwiYXVkIjpbImp1cmlkaWthIiwiaHR0cHM6Ly9qdXJpZGlrYS5ldS5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNTE2NzI1MzIyLCJleHAiOjE1MTY4MTE3MjIsImF6cCI6ImEzaVBQM1N0OGV0MFBBbTFpWjV4eHpYUkw5VFhkUGdDIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCBhZGRyZXNzIHBob25lIiwiZ3R5IjoicGFzc3dvcmQifQ.c-OvEcKEMOUSjQ3FlmnHvQIsbS_HaOASd8UOSfsyXw-OpVHOjeGlX7-l7VjuWYJrhA3JT6uCFCJLgICr8xayWVV_jAHAFL-Ct_RlwNX3vbYed2UG6gpVuZD4qiP5k7jtIMbOxPJDzpiP2AOd-ZoP0aUQV29VrCHMMCZwjqWpBaFDl8SvWKf-SNsVzTVGawgJ7O1WpnNAUI6RlPwvKtw14eD_kO09ZErY-mQn40vEY-523qFxtBYNSjc1VMVWCt2rDgg9KjcMNTo7QHk_LLHnLSloVIqIQ0xKa1k-omrZbJe2pZL4HL_6Bp697Li3V7Vw60dPGwHhh1lnMdjtY3I_wg',
                                 'eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlF6a3dNRUkxUWtSR09UWTNRak5DTlVRNE9EVkRNRVUxUTBGRlFUZERNREJHTmtRd09EZ3dPQSJ9.eyJodHRwczovL2p1cmlkaWthL3Rlcm1zIjpmYWxzZSwiZ2l2ZW5fbmFtZSI6Ik9kZGdlaXIiLCJmYW1pbHlfbmFtZSI6IkdpdGxlc3RhZCIsIm5pY2tuYW1lIjoiT2RkZ2VpciIsIm5hbWUiOiJPZGRnZWlyIEdpdGxlc3RhZCIsInBpY3R1cmUiOiJodHRwczovL3MuZ3JhdmF0YXIuY29tL2F2YXRhci9mNzJjNGU5OTYzNDA3NjQwZWNhZjk3OTE4ODhiMGY3MT9zPTQ4MCZyPXBnJmQ9aHR0cHMlM0ElMkYlMkZjZG4uYXV0aDAuY29tJTJGYXZhdGFycyUyRm9nLnBuZyIsInVwZGF0ZWRfYXQiOiIyMDE4LTAxLTIzVDE2OjM1OjE5LjM3MloiLCJlbWFpbCI6Im9kZGdlaXIuZ2l0bGVzdGFkQGZkc2ZhaWJ0ZXcuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJpc3MiOiJodHRwczovL2p1cmlkaWthLmV1LmF1dGgwLmNvbS8iLCJzdWIiOiJhdXRoMHw1YTY3NjQ0M2RmNTMwZjVmNzJhZTRiYWYiLCJhdWQiOiJhM2lQUDNTdDhldDBQQW0xaVo1eHh6WFJMOVRYZFBnQyIsImlhdCI6MTUxNjcyNTMyMiwiZXhwIjoxNTE2NzYxMzIyfQ.XJ8E3giwTnKSoCC5QbL3jW3VTtiX12YBker5MNWe40apyDTtBpWKk0AmxCNYCC9zz8-Z1QilLuTsCUnjl7cqxNLNdsSBYUSYdzz0LFC5iymJgX-2YHP7msr3-K704egB2cfsz0XB-99Oq0HuHfDJNJxW6hELWJwLaqQaKOKHol_uEIKX5PX9MEwA0UQyLeFnxo0OtP37NYvOcJ8kT841xmWZ0lVOI5cTmm8RqyMVzr8R_ENS0xKJCTm5GQOLWX_cTrblLKseoAG77pH57Ueg4XertI41O9Nx3drf6bmUywXWcdpcHQKX0hiESzOB49EfNcyq08E7AbXlgKcPpJwJHw');

INSERT INTO customer (id, customer_number, name, signup, contact_id, ip_e_username, ip_e_password, subscription_status, account_manager, potential_users, subscription_package_id, market_category_id)
VALUES ('08788418-f6e3-4ae3-95bc-f06ea4b81f17', 'test-2000', 'CustomerWithNulls', 'BBBBB', '32c42ef9-cc97-40bf-9590-66ff7a84ae04',
        null, null,
        'TRIAL', null, '100', null, null);

INSERT INTO customer_products (customer_id, product_id) VALUES ('8374627f-a31f-43eb-ad6c-022f367e3c18', '0d97d518-be2e-4da0-83a1-a0b895269097');

INSERT INTO subscription_plan (auth0_group_id, stripe_plan_id, name, pricing, version)
    VALUES ('533d5fa8-acd6-4efd-a069-18f097384747', 'plan_Bl85KcqjVUlvEk', 'basis', 'monthly', 1);
INSERT INTO subscription_plan (auth0_group_id, stripe_plan_id, name, pricing, version)
    VALUES ('b12ed396-ec12-448d-942b-c7740d492d0a', 'plan_Fl85KcqjVUlvEj', 'basis', 'monthly', 2);
