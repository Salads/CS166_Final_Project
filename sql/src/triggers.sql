DROP TRIGGER IF EXISTS user_role_trigger ON Users;
DROP TRIGGER IF EXISTS trackinginfo_last_update_time ON TrackingInfo;
DROP TRIGGER IF EXISTS generate_rentalorderid ON RentalOrder;
DROP TRIGGER IF EXISTS generate_trackingid ON TrackingInfo;
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
        NEW.dueDate := NOW() + INTERVAL '1 month';

        RETURN NEW;
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION random_city()
    RETURNS TEXT AS
    $BODY$
    BEGIN
        RETURN (ARRAY['New York City, New York',
                      'Los Angeles, California',
                      'Chicago, Illinois',
                      'Houston, Texas',
                      'Phoenix, Arizona',
                      'Philadelphia, Pennsylvania'])[floor(random()*6)+1];
    END;
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION random_courier()
    RETURNS TEXT AS
    $BODY$
    BEGIN
        RETURN (ARRAY['FedEx', 'UPS', 'DHL', 'USPS', 'TNT'])[floor(random()*5)+1];
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

CREATE TRIGGER generate_rentalorderid
BEFORE INSERT ON RentalOrder
FOR EACH ROW
EXECUTE PROCEDURE generate_rentalorder();

CREATE TRIGGER generate_trackingid
BEFORE INSERT ON TrackingInfo
FOR EACH ROW 
EXECUTE PROCEDURE generate_trackinginfo();