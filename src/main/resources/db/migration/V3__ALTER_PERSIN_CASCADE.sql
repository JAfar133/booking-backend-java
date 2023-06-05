ALTER TABLE booking
DROP CONSTRAINT booking_person_id_fkey,
ADD CONSTRAINT booking_person_id_fkey
FOREIGN KEY (person_id)
REFERENCES person (id)
ON DELETE CASCADE;
