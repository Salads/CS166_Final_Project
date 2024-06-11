DROP TRIGGER IF EXISTS user_role_trigger ON Users;
DROP TRIGGER IF EXISTS trackinginfo_last_update_time ON TrackingInfo;
DROP TRIGGER IF EXISTS trigger_rental_order ON RentalOrder;
DROP TRIGGER IF EXISTS trigger_generate_tracking_info ON TrackingInfo;
DROP SEQUENCE IF EXISTS rentalorder_seq;
DROP SEQUENCE IF EXISTS trackingid_seq;

DROP FUNCTION IF EXISTS random_city();
DROP FUNCTION IF EXISTS random_courier();

CREATE SEQUENCE rentalorder_seq START WITH 4147 INCREMENT BY 1;
CREATE SEQUENCE trackingid_seq START WITH 4147 INCREMENT BY 1;

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

CREATE OR REPLACE FUNCTION generate_rentalorderid()
    RETURNS TEXT AS
    $BODY$
    DECLARE
        next_val INT;
    BEGIN
        next_val := nextval('rentalorder_seq');
        RETURN 'gamerentalorder' || LPAD(next_val::TEXT, 4, '0');
    END
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION generate_rentalorder_time()
    RETURNS TRIGGER AS
    $BODY$
    DECLARE 
        next_val INT;
    BEGIN
        NEW.orderTimestamp := NOW();
        NEW.dueDate := NOW() + INTERVAL '1 month';

        RETURN NEW;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION random_city()
    RETURNS TEXT AS
    $BODY$
    DECLARE 
        result VARCHAR(60);
    BEGIN
        SELECT city INTO result
        FROM Cities 
        ORDER BY RANDOM()
        LIMIT 1;
        return result;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION random_courier()
    RETURNS TEXT AS
    $BODY$
    DECLARE 
        result VARCHAR(60);
    BEGIN
        SELECT courierNames INTO result
        FROM Couriers 
        ORDER BY RANDOM()
        LIMIT 1;
        return result;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION generate_trackinginfo()
    RETURNS TRIGGER AS 
    $BODY$
    BEGIN 
        NEW.trackingID := 'trackingid' || LPAD(nextval('trackingid_seq')::TEXT, 4, '0');
        NEW.status := 'Pending';
        NEW.currentLocation := random_city();
        NEW.courierName := random_courier();
        NEW.additionalComments := '';
        NEW.lastUpdateDate := NOW();
        RETURN NEW;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER user_role_trigger 
BEFORE INSERT ON Users
FOR EACH ROW
EXECUTE PROCEDURE create_role(); 

CREATE TRIGGER trigger_generate_tracking_info
BEFORE INSERT ON TrackingInfo
FOR EACH ROW
EXECUTE PROCEDURE generate_trackinginfo(); 

CREATE TRIGGER trackinginfo_last_update_time 
AFTER UPDATE ON TrackingInfo
FOR EACH ROW
EXECUTE PROCEDURE update_tracking_info_update_time(); 

CREATE TRIGGER trigger_rental_order
BEFORE INSERT ON RentalOrder
FOR EACH ROW
EXECUTE PROCEDURE generate_rentalorder_time();