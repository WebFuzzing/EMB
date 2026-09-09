// MODIFIED (WFD-added, not part of the original SUT): the same minimal seed the drivers create -
// only what the API can never produce itself, i.e. ADMIN accounts (/register always writes
// is_admin=false) and reviews older than the glow-up cutoff. Profiles, likes, matches and recent
// reviews are left for the search to create.
// No "_class" field: MongoConfig installs DefaultMongoTypeMapper(null), which strips it.
db = db.getSiblingDB('LoveMining');

const mo = function (m) { return new Date(Date.now() - m * 30 * 24 * 60 * 60 * 1000); };

const ADMIN1_HASH = '$2a$10$Q5Uui.PMfXK0RjKqJek4Cusq5tKmTeUXwm6tdD.HA6LrLew7JK0me'; // Wfd-Admin-Pass1
const ADMIN2_HASH = '$2a$10$Zbu3QxMgPmo1ZunqPxn2SeWuEGJP9RHsBeoCOoKdlfuF8BNn0AnFy'; // Wfd-Admin-Pass2
const USER1_HASH = '$2a$10$3gCReephTyeV2WcmQklcJuEjALyr.sTkTyUKuVmCX7fFyPSAYPFp6';   // Wfd-User-Pass1
const USER2_HASH = '$2a$10$t1QO4YBNJaM5gQk6v9I9EeiBjHO.1EWmKcURu8/NfMaaNrpvRIC3e';   // Wfd-User-Pass2

db.getCollection('users').insertMany([
  { "_id": "wfd_admin1", "Email": "wfd_admin1@wfd.invalid", "Password": ADMIN1_HASH, "is_admin": true },
  { "_id": "wfd_admin2", "Email": "wfd_admin2@wfd.invalid", "Password": ADMIN2_HASH, "is_admin": true },
  {
    "_id": "wfd_user1", "Email": "wfd_user1@wfd.invalid", "Password": USER1_HASH, "is_admin": false,
    "age": 28, "sex": "m", "orientation": "straight", "status": "single",
    "city": "pisa", "state": "tuscany",
    "essay0": "I love music and travel.", "interests": ["music", "travel"]
  },
  {
    "_id": "wfd_user2", "Email": "wfd_user2@wfd.invalid", "Password": USER2_HASH, "is_admin": false,
    "age": 27, "sex": "f", "orientation": "straight", "status": "single",
    "city": "pisa", "state": "tuscany",
    "essay0": "Music and art are my life.", "interests": ["music", "art"]
  }
]);

// Reviews record no author: ReviewDocument has no such field, the link lives in reviews_made.
// Three on one target, spanning the cutoff, is the glow-up minimum. Dates must stay relative.
db.getCollection('reviews').insertMany([
  { "_id": "wfd_rv1", "target_id": "wfd_user1", "rating": 2, "comment": "Conversation was hard work.", "review_date": mo(9) },
  { "_id": "wfd_rv2", "target_id": "wfd_user1", "rating": 1, "comment": "Not my type at all.", "review_date": mo(8) },
  { "_id": "wfd_rv3", "target_id": "wfd_user1", "rating": 5, "comment": "Much better than I expected.", "review_date": mo(1) }
]);
