import os
import sys
import PyPDF2

sys.stdout.reconfigure(encoding='utf-8')

def pdf_to_text(pdf_path, txt_path):
    try:
        with open(pdf_path, 'rb') as file:
            pdf_reader = PyPDF2.PdfReader(file)
            text = ""
            for page in pdf_reader.pages:
                page_text = page.extract_text()
                if page_text:
                    text += page_text
            with open(txt_path, 'w', encoding='utf-8') as txt_file:
                txt_file.write(text)
        return True
    except Exception as e:
        print(f"转换失败 {pdf_path}: {str(e)}")
        return False

pdf_dir = r'E:\java\project\校园旧书漂流共享系统\资料\参考文献'
txt_dir = r'E:\java\project\校园旧书漂流共享系统\资料\参考文献\txt'

if not os.path.exists(txt_dir):
    os.makedirs(txt_dir)

success_count = 0
fail_count = 0

for filename in os.listdir(pdf_dir):
    if filename.lower().endswith('.pdf'):
        pdf_path = os.path.join(pdf_dir, filename)
        txt_file = filename.replace('.pdf', '.txt').replace('.PDF', '.txt')
        txt_path = os.path.join(txt_dir, txt_file)
        print(f"正在转换: {filename}")
        if pdf_to_text(pdf_path, txt_path):
            print(f"  成功 -> {txt_file}")
            success_count += 1
        else:
            fail_count += 1

print(f"\n转换完成！成功: {success_count}, 失败: {fail_count}")
