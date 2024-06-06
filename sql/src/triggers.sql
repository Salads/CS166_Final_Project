DROP TRIGGER IF EXISTS user_role_trigger ON Users;

CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION create_role()
    RETURNS TRIGGER AS
    $BODY$
    BEGIN
       NEW.role := 'Customer';
       RETURN NEW;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER user_role_trigger 
BEFORE INSERT ON Users
FOR EACH ROW
EXECUTE PROCEDURE insert_pnumber(); 
