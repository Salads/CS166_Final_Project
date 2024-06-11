DROP TRIGGER IF EXISTS user_role_trigger ON Users;
DROP TRIGGER IF EXISTS trackinginfo_last_update_time ON TrackingInfo;
DROP TRIGGER IF EXISTS generate_rentalorderid ON RentalOrder;
DROP SEQUENCE IF EXISTS rentalorder_seq;

CREATE SEQUENCE rentalorder_seq START WITH 4147 INCREMENT BY 1;

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

CREATE OR REPLACE FUNCTION generate_rentalorder()
    RETURNS TRIGGER AS
    $BODY$
    DECLARE 
        next_val INT;
    BEGIN
        next_val := nextval('rentalorder_seq');
        NEW.rentalOrderID := 'gamerentalorder' || LPAD(next_val::TEXT, 4, '0');
        NEW.orderTimestamp := NOW();
        NEW.dueDate := DATEADD(m, 1, GETDATE());

        RETURN NEW;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION random_city()
    RETURNS TRIGGER AS
    $BODY$
    DECLARE 
        result VARCHAR(60);
    BEGIN
        SELECT city INTO result
        FROM Cities 
        ORDER BY RAND()
        LIMIT 1;
        return result;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION random_courier()
    RETURNS TRIGGER AS
    $BODY$
    DECLARE 
        result VARCHAR(60);
    BEGIN
        SELECT courierNames INTO result
        FROM Couriers 
        ORDER BY RAND()
        LIMIT 1;
        return result;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION generate_trackinginfo()
    RETURNS TRIGGER AS 
    $BODY$
    BEGIN 
        NEW.trackingID := 'trackingid' || regexp_replace(NEW.rentalorderID, '\D', '', 'g');
        NEW.status := 'Pending';
        NEW.currentLocation := random_city();
        NEW.courierName := random_courier();
        NEW.lastUpdateDate := NOW();
        NEW.additionalComments := '';
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

CREATE TRIGGER generate_rentalorderid
BEFORE INSERT ON RentalOrder
FOR EACH ROW
EXECUTE PROCEDURE generate_rentalorder();