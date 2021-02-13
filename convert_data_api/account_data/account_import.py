import json
import mysql.connector

conn = mysql.connector.connect(
    user='root',
    password='1111',
    host='127.0.0.1',
    database='k2_tech_store',
    auth_plugin='mysql_native_password'
)
cur = conn.cursor()

try:
    cur.execute('SET FOREIGN_KEY_CHECKS = 0;')
    cur.execute('truncate table account;')
    cur.execute('SET FOREIGN_KEY_CHECKS = 1;')

    with open('data_customer.json') as data:
        query = "insert into account(" \
                "user_name, " \
                "full_name, " \
                "date_of_birth, " \
                "gender, " \
                "role_id, " \
                "password, " \
                "salt, " \
                "email, " \
                "phone, " \
                "address, " \
                "avatar_path, " \
                "status, " \
                "province_id, " \
                "district_id, " \
                "commune_id, " \
                "create_date, " \
                "create_by, " \
                "update_date, " \
                "update_by) " \
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        json = json.loads(data)
        for a in data:
            json = json.loads(a)
            print(json)
    conn.commit()
    conn.close()
except Exception as e:
    print(e)
