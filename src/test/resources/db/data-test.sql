-- Users (UUIDs + BCrypt encrypted passwords - all passwords are 'Password123!')
INSERT INTO user (id, email, password, role, full_name, confirmed, address, tlf) 
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'admin@test.com', '$2a$10$BnDm9h8qqxFfvZPQvRJbDei4JSUSf4GkP8sBvwsF3JJoAZ5xkxKIW', 'SUPERADMIN', 'Admin User', 1, 'Admin Address 123', '12345678');

INSERT INTO user (id, email, password, role, full_name, confirmed, address, tlf) 
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d480', 'user@test.com', '$2a$10$BnDm9h8qqxFfvZPQvRJbDei4JSUSf4GkP8sBvwsF3JJoAZ5xkxKIW', 'USER', 'Test User', 1, 'Test Address 1', '87654321');

INSERT INTO user (id, email, password, role, full_name, confirmed, address, tlf) 
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d481', 'user2@test.com', '$2a$10$BnDm9h8qqxFfvZPQvRJbDei4JSUSf4GkP8sBvwsF3JJoAZ5xkxKIW', 'USER', 'Test User 2', 1, 'Test Address 2', '98765432');

INSERT INTO user (id, email, password, role, full_name, confirmed, address, tlf) 
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d482', 'admin2@test.com', '$2a$10$BnDm9h8qqxFfvZPQvRJbDei4JSUSf4GkP8sBvwsF3JJoAZ5xkxKIW', 'ADMIN', 'Admin User 2', 1, 'Admin Address 2', '23456789');

-- Unconfirmed user with token
INSERT INTO user (id, email, password, role, full_name, confirmed, confirmation_token, token_expiry, address, tlf) 
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d483', 'unconfirmed@test.com', '$2a$10$BnDm9h8qqxFfvZPQvRJbDei4JSUSf4GkP8sBvwsF3JJoAZ5xkxKIW', 'USER', 'Unconfirmed User', 0, 'test-confirmation-token', DATE_ADD(NOW(), INTERVAL 1 DAY), 'Pending Address', '11223344');

-- Households
INSERT INTO household (id, name, address, number_of_members, owner_id) 
VALUES ('h47ac10b-58cc-4372-a567-0e02b2c3d479', 'Test Household 1', 'Household Address 1', 2, 'f47ac10b-58cc-4372-a567-0e02b2c3d480');

INSERT INTO household (id, name, address, number_of_members, owner_id) 
VALUES ('h47ac10b-58cc-4372-a567-0e02b2c3d480', 'Test Household 2', 'Household Address 2', 1, 'f47ac10b-58cc-4372-a567-0e02b2c3d481');

-- Update user with household reference
UPDATE user SET household_id = 'h47ac10b-58cc-4372-a567-0e02b2c3d479' WHERE id = 'f47ac10b-58cc-4372-a567-0e02b2c3d480';
UPDATE user SET household_id = 'h47ac10b-58cc-4372-a567-0e02b2c3d480' WHERE id = 'f47ac10b-58cc-4372-a567-0e02b2c3d481';

-- Unregistered household members
INSERT INTO unregistered_household_member (full_name, household_id) 
VALUES ('Unregistered Member 1', 'h47ac10b-58cc-4372-a567-0e02b2c3d479');

INSERT INTO unregistered_household_member (full_name, household_id) 
VALUES ('Unregistered Member 2', 'h47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Items
INSERT INTO item (name, item_type, caloric_amount) VALUES ('Water Bottle', 'LIQUIDS', NULL);
INSERT INTO item (name, item_type, caloric_amount) VALUES ('Canned Beans', 'FOOD', 200);
INSERT INTO item (name, item_type, caloric_amount) VALUES ('Rice', 'FOOD', 350);
INSERT INTO item (name, item_type, caloric_amount) VALUES ('Bandages', 'FIRST AID', NULL);
INSERT INTO item (name, item_type, caloric_amount) VALUES ('Flashlight', 'TOOL', NULL);

-- Storage (household inventory)
INSERT INTO storage (item_id, household_id, unit, amount, expiration_date, date_added) 
VALUES (1, 'h47ac10b-58cc-4372-a567-0e02b2c3d479', 'liters', 10, DATE_ADD(NOW(), INTERVAL 1 YEAR), NOW());

INSERT INTO storage (item_id, household_id, unit, amount, expiration_date, date_added) 
VALUES (2, 'h47ac10b-58cc-4372-a567-0e02b2c3d479', 'cans', 5, DATE_ADD(NOW(), INTERVAL 6 MONTH), NOW());

INSERT INTO storage (item_id, household_id, unit, amount, expiration_date, date_added) 
VALUES (4, 'h47ac10b-58cc-4372-a567-0e02b2c3d479', 'boxes', 2, DATE_ADD(NOW(), INTERVAL 3 YEAR), NOW());

INSERT INTO storage (item_id, household_id, unit, amount, expiration_date, date_added) 
VALUES (3, 'h47ac10b-58cc-4372-a567-0e02b2c3d480', 'kilograms', 3, DATE_ADD(NOW(), INTERVAL 2 YEAR), NOW());

INSERT INTO storage (item_id, household_id, unit, amount, expiration_date, date_added) 
VALUES (5, 'h47ac10b-58cc-4372-a567-0e02b2c3d480', 'pieces', 1, DATE_ADD(NOW(), INTERVAL 10 YEAR), NOW());

-- Scenarios
INSERT INTO scenario (name, description, to_do, packing_list, icon_name) 
VALUES ('Power Outage', 'Extended loss of electricity', 'Find alternative light sources, keep refrigerator closed, check on neighbors', 'Flashlights, batteries, portable charger, blankets', 'power-outage');

INSERT INTO scenario (name, description, to_do, packing_list, icon_name) 
VALUES ('Flood', 'Rising water levels and flooding', 'Move to higher ground, avoid driving through water, listen to emergency broadcasts', 'Life vests, waterproof containers, drinking water, non-perishable food', 'flood');

INSERT INTO scenario (name, description, to_do, packing_list, icon_name) 
VALUES ('Fire', 'Wildfire or urban fire emergency', 'Evacuate immediately if instructed, close all windows and doors, follow evacuation routes', 'Important documents, medications, face masks, change of clothes', 'fire');

-- Incidents
INSERT INTO incident (name, description, latitude, longitude, impact_radius, severity, started_at, ended_at, scenario_id) 
VALUES ('Downtown Power Outage', 'Major power outage affecting downtown area', 63.4305, 10.3951, 2.5, 'YELLOW', NOW(), NULL, 1);

INSERT INTO incident (name, description, latitude, longitude, impact_radius, severity, started_at, ended_at, scenario_id) 
VALUES ('River Flooding', 'Nidelva river flooding after heavy rainfall', 63.4269, 10.4103, 1.8, 'RED', NOW(), NULL, 2);

-- Map icons
INSERT INTO map_icon (type, address, latitude, longitude, description, opening_hours, contact_info) 
VALUES ('HOSPITAL', 'Hospital Street 1', 63.4223, 10.3915, 'Main City Hospital', '24/7', '+47 123 45 678');

INSERT INTO map_icon (type, address, latitude, longitude, description, opening_hours, contact_info) 
VALUES ('SHELTER', 'Shelter Road 5', 63.4400, 10.4050, 'Emergency Shelter at Community Center', '24/7 during emergencies', '+47 987 65 432');

INSERT INTO map_icon (type, address, latitude, longitude, description, opening_hours, contact_info) 
VALUES ('FOODSTATION', 'Station Square 10', 63.4350, 10.3850, 'Food Distribution Point', '09:00-18:00', '+47 555 12 345');

-- Membership requests
INSERT INTO membership_request (household_id, sender_id, receiver_id, type, status, created_at) 
VALUES ('h47ac10b-58cc-4372-a567-0e02b2c3d479', 'f47ac10b-58cc-4372-a567-0e02b2c3d482', 'f47ac10b-58cc-4372-a567-0e02b2c3d480', 'JOIN_REQUEST', 'PENDING', NOW());

INSERT INTO membership_request (household_id, sender_id, receiver_id, type, status, created_at) 
VALUES ('h47ac10b-58cc-4372-a567-0e02b2c3d480', 'f47ac10b-58cc-4372-a567-0e02b2c3d480', 'f47ac10b-58cc-4372-a567-0e02b2c3d483', 'INVITATION', 'PENDING', NOW());

-- Notifications
INSERT INTO notification (user_id, type, is_read, message) 
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d480', 'MEMBERSHIP_REQUEST', 0, 'Admin User 2 has requested to join your household');

INSERT INTO notification (user_id, type, is_read, message) 
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d481', 'INCIDENT', 0, 'New incident reported: River Flooding');

INSERT INTO notification (user_id, type, is_read, message) 
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d483', 'HOUSEHOLD', 0, 'You have been invited to join Test Household 2');

-- News
INSERT INTO news (title, url, content, source) 
VALUES ('Storm Warning for Trondheim Area', 'https://example.com/storm-warning', 'A severe storm is expected to hit the Trondheim area tomorrow. Residents are advised to secure loose objects and prepare for power outages.', 'National Weather Service');

INSERT INTO news (title, url, content, source) 
VALUES ('City Updates Emergency Response Plan', 'https://example.com/emergency-plan', 'The city has updated its emergency response plan to better handle crisis situations. Citizens are encouraged to familiarize themselves with evacuation routes.', 'City Council');

-- Prep groups
INSERT INTO prep_group (name, owner_id) 
VALUES ('Neighborhood Watch', 'f47ac10b-58cc-4372-a567-0e02b2c3d480');

INSERT INTO prep_group (name, owner_id) 
VALUES ('Community Helpers', 'f47ac10b-58cc-4372-a567-0e02b2c3d481');

-- Prep group household relationships
INSERT INTO prep_group_household (prep_group_id, household_id) 
VALUES (1, 'h47ac10b-58cc-4372-a567-0e02b2c3d479');

INSERT INTO prep_group_household (prep_group_id, household_id) 
VALUES (2, 'h47ac10b-58cc-4372-a567-0e02b2c3d480');

-- Prep group storage relationships
INSERT INTO prep_group_storage (prep_group_id, storage_id) 
VALUES (1, 1);

INSERT INTO prep_group_storage (prep_group_id, storage_id) 
VALUES (2, 4);