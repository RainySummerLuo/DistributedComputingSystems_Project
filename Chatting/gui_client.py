# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'gui.ui'
#
# Created by: PyQt5 UI code generator 5.11.3
#
# WARNING! All changes made in this file will be lost!

import xmlrpc.client

from PyQt5 import QtCore, QtGui, QtWidgets

s = xmlrpc.client.ServerProxy('http://localhost:8000')

class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName("MainWindow")
        MainWindow.resize(481, 423)
        MainWindow.setLayoutDirection(QtCore.Qt.LeftToRight)
        self.centralwidget = QtWidgets.QWidget(MainWindow)
        self.centralwidget.setObjectName("centralwidget")
        self.verticalLayoutWidget = QtWidgets.QWidget(self.centralwidget)
        self.verticalLayoutWidget.setGeometry(QtCore.QRect(10, 380, 461, 31))
        self.verticalLayoutWidget.setObjectName("verticalLayoutWidget")
        self.verticalLayout = QtWidgets.QVBoxLayout(self.verticalLayoutWidget)
        self.verticalLayout.setContentsMargins(0, 0, 0, 0)
        self.verticalLayout.setObjectName("verticalLayout")
        self.horizontalLayout = QtWidgets.QHBoxLayout()
        self.horizontalLayout.setObjectName("horizontalLayout")
        self.lineEdit = QtWidgets.QLineEdit(self.verticalLayoutWidget)
        font = QtGui.QFont()
        font.setFamily("华文细黑")
        font.setPointSize(11)
        self.lineEdit.setFont(font)
        self.lineEdit.setObjectName("lineEdit")
        self.horizontalLayout.addWidget(self.lineEdit)
        self.pushButton = QtWidgets.QPushButton(self.verticalLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(11)
        self.pushButton.setFont(font)
        self.pushButton.setObjectName("pushButton")
        self.horizontalLayout.addWidget(self.pushButton)
        self.verticalLayout.addLayout(self.horizontalLayout)
        self.textEdit = QtWidgets.QTextEdit(self.centralwidget)
        self.textEdit.setGeometry(QtCore.QRect(10, 10, 461, 361))
        font = QtGui.QFont()
        font.setFamily("华文细黑")
        font.setPointSize(11)
        self.textEdit.setFont(font)
        self.textEdit.setReadOnly(True)
        self.textEdit.setObjectName("textEdit")
        MainWindow.setCentralWidget(self.centralwidget)

        # MainWindow.setWindowFlags(QtCore.Qt.WindowMinimizeButtonHint)
        MainWindow.setWindowFlags(QtCore.Qt.WindowCloseButtonHint)
        MainWindow.setFixedSize(MainWindow.width(), MainWindow.height())

        self.retranslateUi(MainWindow)
        self.pushButton.clicked.connect(self.send_button_click)
        self.lineEdit.setFocus()
        self.lineEdit.returnPressed.connect(self.send_button_click)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)

    def retranslateUi(self, MainWindow):
        _translate = QtCore.QCoreApplication.translate
        MainWindow.setWindowTitle(_translate("MainWindow", "MainWindow"))
        self.pushButton.setText(_translate("MainWindow", "Send"))

    def send_button_click(self):
        sth = self.lineEdit.text()
        self.lineEdit.clear()
        self.textEdit.append("我说: " + sth)
        reply = s.say(sth)
        self.textEdit.append("她说: " + reply)
        self.lineEdit.setFocus()


if __name__ == "__main__":
    import sys
    app = QtWidgets.QApplication(sys.argv)
    MainWindow = QtWidgets.QMainWindow()
    ui = Ui_MainWindow()
    ui.setupUi(MainWindow)
    MainWindow.show()
