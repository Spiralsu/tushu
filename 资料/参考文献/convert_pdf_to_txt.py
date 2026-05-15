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

new_pdfs = [
    'informit.760521798634627.pdf',
    'laurenti2020.pdf',
    'UNISYNC_UNLEASH_THE_CAMPUS_SPIRIT_SHARIN.pdf',
    'Boyan_Wei_thesis_spring_2022final.pdf',
    'Ajiboye and Emmanuel 1 - 21-1_230503_121048.pdf'
]

success_count = 0
fail_count = 0

for pdf_file in new_pdfs:
    pdf_path = os.path.join(pdf_dir, pdf_file)
    if os.path.exists(pdf_path):
        txt_file = pdf_file.replace('.pdf', '.txt')
        txt_path = os.path.join(txt_dir, txt_file)
        print(f"正在转换: {pdf_file}")
        if pdf_to_text(pdf_path, txt_path):
            print(f"  成功 -> {txt_file}")
            success_count += 1
        else:
            fail_count += 1
    else:
        print(f"  文件不存在: {pdf_file}")
        fail_count += 1

print(f"\n转换完成！成功: {success_count}, 失败: {fail_count}")