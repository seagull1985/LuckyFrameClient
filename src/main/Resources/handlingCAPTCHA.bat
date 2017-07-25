@echo 跳转到Tesseract所有的磁盘以及目录
e:
cd "\Program Files (x86)\Tesseract-OCR"
@echo 此目录为当前工程所在的根目录
tesseract E:\workspace\TestFrame\CAPTCHA.png E:\workspace\TestFrame\CAPTCHA -1
exit