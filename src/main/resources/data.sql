INSERT INTO "genres" ("id", "name") VALUES (1, 'Комедия') ON CONFLICT DO NOTHING; -- UPDATE SET name = EXCLUDED.name;
INSERT INTO "genres" ("id", "name") VALUES (2, 'Драма') ON CONFLICT DO NOTHING; --UPDATE SET name = EXCLUDED.name;
INSERT INTO "genres" ("id", "name") VALUES (3, 'Мультфильм') ON CONFLICT DO NOTHING; --UPDATE SET name = EXCLUDED.name;
INSERT INTO "genres" ("id", "name") VALUES (4, 'Триллер') ON CONFLICT DO NOTHING; --UPDATE SET name = EXCLUDED.name;
INSERT INTO "genres" ("id", "name") VALUES (5, 'Документальный') ON CONFLICT DO NOTHING; --UPDATE SET name = EXCLUDED.name;
INSERT INTO "genres" ("id", "name") VALUES (6, 'Боевик') ON CONFLICT DO NOTHING; --UPDATE SET name = EXCLUDED.name;

INSERT INTO "MPA_ratings" ("id", "name") VALUES (1, 'G') ON CONFLICT DO NOTHING;
INSERT INTO "MPA_ratings" ("id", "name") VALUES (2, 'PG') ON CONFLICT DO NOTHING;
INSERT INTO "MPA_ratings" ("id", "name") VALUES (3, 'PG-13') ON CONFLICT DO NOTHING;
INSERT INTO "MPA_ratings" ("id", "name") VALUES (4, 'R') ON CONFLICT DO NOTHING;
INSERT INTO "MPA_ratings" ("id", "name") VALUES (5, 'NC-17') ON CONFLICT DO NOTHING;