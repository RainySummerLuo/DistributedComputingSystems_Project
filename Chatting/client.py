#!/usr/bin/env python
# -*- coding: utf-8 -*-

from PyQt5.QtWidgets import QApplication, QMainWindow
from gui_client import *

s = xmlrpc.client.ServerProxy('http://localhost:8000')

if __name__ == '__main__':
    import sys
    app = QApplication(sys.argv)
    mainwindow = QMainWindow()
    ui = Ui_MainWindow()
    ui.setupUi(mainwindow)
    mainwindow.show()
    sys.exit(app.exec_())

# while 1:
#     sth = input("我说: ")
#     reply = s.say(sth)
#     print("TA说: ", reply)
