package myDB;

/**
 * Created by cwsu on 2017/5/16.
 */

public interface dbUserSchema {

    static final String[] UserSchemaNameList =
            {"username","password","key"};
    static final String[] UserSchemaTypeList =
            {"VARCHAR","VARCHAR","VARCHAR"};
    static final Integer[] UserSchemaLengthList =
            {20,20,512};
    static final Integer USER_SCHEMA_LENGTH = UserSchemaLengthList.length;

}
