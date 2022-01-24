#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import socket
import pymysql
# from sshtunnel import SSHTunnelForwarder

import threading

resInfo = []

tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# 绑定
tcp_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, True)
# 端口回收
tcp_socket.bind(('', 12345))
# 设置最大连接数，超过后排队
tcp_socket.listen(128)


def sendInfo(s, ip):
    global resInfo
    print("连接地址: %s" % str(ip))
    # server = SSHTunnelForwarder(
    #     ssh_address_or_host=('101.43.18.202', 22),  # 指定ssh登录的跳转机的address
    #     ssh_username='lighthouse',  # 跳转机的用户
    #     ssh_password='1234',  # 跳转机的密码
    #     local_bind_address=('127.0.0.1', 3306),  # 映射到本机的地址和端口
    #     remote_bind_address=('101.43.18.202', 3306))  # 数据库的地址和端口
    # server.start()  # 启用SSH
    # 数据库账户信息设置
    db = pymysql.connect(
        host="127.0.0.1",  # 映射地址local_bind_address IP
        port=3306,  # 映射地址local_bind_address端口
        user="lighthouse",
        passwd="mysql",
        database='test',  # 需要连接的实例名
        charset='utf8')
    xh = str(s.recv(1024).decode())
    infoRequest = xh + '%'
    grade = xh[0:4]
    cursor = db.cursor()
    if grade == '2021':
        sql = "select * from grade_2021 where id like '%s'" % (infoRequest)
    if grade == '2020':
        sql = "select * from grade_2020 where id like '%s'" % (infoRequest)
    if grade == '2019':
        sql = "select * from grade_2019 where id like '%s'" % (infoRequest)
    if grade == '2018':
        sql = "select * from grade_2018 where id like '%s'" % (infoRequest)
    print(sql)
    cursor.execute(sql)
    while 1:
        res = cursor.fetchone()
        if res is None:
            cursor.close()
            db.close()
            # 表示已经取完结果集
            break
        resInfo.append(res)
    s.send(str(resInfo).encode('utf-8'))
    print("发送成功")
    s.close()
    resInfo = []


def main():
    while True:
        client_socket, ip_address = tcp_socket.accept()
        thread = threading.Thread(target=sendInfo, args=(client_socket, ip_address))
        thread.start()


if __name__ == '__main__':
    print("HiCLass TCP server bound 12345...")
    main()
