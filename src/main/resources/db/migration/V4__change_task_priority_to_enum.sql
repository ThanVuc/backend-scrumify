ALTER TABLE tasks
    ALTER COLUMN priority TYPE VARCHAR(16)
    USING CASE
        WHEN priority = 1 THEN 'LOW'
        WHEN priority = 2 THEN 'MEDIUM'
        ELSE 'HIGH'
    END;
