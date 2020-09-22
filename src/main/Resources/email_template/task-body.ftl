<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <style type="text/css">
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, Helvetica, sans-serif;
            font-size: 12px;
            color: #6a6a6a;
        }

        a {
            font-size: 13px
        }
    </style>
</head>
<body>
<table width="780" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
        <td height="10">&nbsp;</td>
    </tr>
    <tr>
        <td valign="top" style="border-left:1px solid #CCC; border-right:1px solid #CCC;border-top:1px solid #CCC;">
            <table width="620" border="0" align="center" cellpadding="0" cellspacing="0">
                <tr>
                    <td height="92" style="background-color:rgba(51,204,255,0.6);">
                        <a href="http://www.luckyframe.cn" title="LuckyFrame" target="_blank"><img
                                    src="http://${webip}:${webport}/img/maillogo.png" alt="LuckyFrame" width="300"
                                    height="92" border="0"/></a></td>
                </tr>
                <tr>
                    <td height="1" colspan="2">
                        <hr style="border-bottom:5px solid #f1f1f1; display:block;"/>
                    </td>
                </tr>
                <tr>
                    <td height="20" colspan="2">&nbsp;</td>
                </tr>
                <tr>
                    <td height="40" colspan="2" style="font-size:12px; text-indent:25px;">
                        <div style="margin: 0px auto; padding: 0px 10px; width: 680px;">
                            <div style="color: rgb(77, 77, 77); line-height: 1.5; font-size: 14px; margin-bottom: 25px;">
                                <strong style="margin-bottom: 15px; display: block;">亲爱的Tester： 您好！以下是自动化任务【${jobname}
                                    】执行情况。</strong>
                                <p><b>自动构建状态： </b> 【<font color='#5CACEE'>${buildstatus}</font>】</p>
                                <p><b>自动重启TOMCAT状态： </b> 【<font color='#5CACEE'>${restartstatus}</font>】</p>
                                <br>
                                <p><b>本次任务预期执行用例共【<font color='#2828FF'>${casecount}</font>】条,耗時【<font color='#2828FF'>${time}</font>】</b></p>
                                <p><b>用例执行成功： </b> 【<font color='#28FF28'>${casesuc}</font>】</p>
                                <p><b>用例执行失败： </b> 【<font color='#FF0000'>${casefail}</font>】</p>
                                <p><b>用例有可能由于脚本原因未成功解析被锁定：</b> 【<font color='#AE57A4'>${caselock}</font>】</p>
                                <p><b>用例由于长时间未收到接口Response未执行完成：</b> 【<font color='#FFAD86'>${caseunex}</font>】</p>
                                <p>&nbsp;</p>
                                <p> 此为自动化平台LuckyFrame的系统邮件，请勿回复</p>
                                <p> 请及时前往<a href='http://${webip}:${webport}${webpath}'>LuckyFrame平台</a>查看您的任务执行的更多细节</p>
                                <p>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td height="10" colspan="2">&nbsp;</td>
                </tr>
                <tr>
                    <td height="40" colspan="2">&nbsp;</td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td height="30" align="center" bgcolor="#E3E3E3" style="font-size:12px; color:#666;">Copyright &copy; &nbsp;&nbsp;<a
                    href='http://www.luckyframe.cn'>LuckyFrame</a>&nbsp;&nbsp;
            2017-2099 All Right Reserved
        </td>
    </tr>
</table>
</body>
</html>
