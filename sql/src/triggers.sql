DROP TRIGGER IF EXISTS user_role_trigger ON Users;
DROP TRIGGER IF EXISTS trackinginfo_last_update_time ON TrackingInfo;

CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION create_role()
    RETURNS TRIGGER AS
    $BODY$
    BEGIN
       NEW.role := 'customer';
       RETURN NEW;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION update_tracking_info_update_time()
    RETURNS TRIGGER AS
    $BODY$
    BEGIN
       NEW.lastUpdateDate := NOW();
       RETURN NEW;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;


CREATE TRIGGER user_role_trigger 
BEFORE INSERT ON Users
FOR EACH ROW
EXECUTE PROCEDURE create_role(); 

CREATE TRIGGER trackinginfo_last_update_time 
BEFORE UPDATE ON TrackingInfo
FOR EACH ROW
EXECUTE PROCEDURE update_tracking_info_update_time(); 
