import tkinter as tk
from tkinter import messagebox
import paramiko
from scp import SCPClient
import time
import json
import os

def start_agent():
    username = entry_username.get()
    password = entry_password.get() ##0718 비밀번호
    port = int(entry_port.get())
    ip_address = entry_ip.get()
    #key_filename = r'C:\Spring\project\KosaProject\src\main\resources\testkey1.pem'
    
    try:
        # Paramiko SSH 클라이언트 생성
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(ip_address, port=port, username=username, password = password)
        
        # SFTP 클라이언트 초기화
        sftp = ssh.open_sftp()
        #여기에 본인 CentOs6.sh 파일경로 지정 해주셔야 되요
        sftp.put(r"C:\Spring\project\KosaProject\src\main\resources\CentOS6.sh", "agent.sh")

        # 명령
        channel = ssh.invoke_shell()
        channel.send('cd ~\n')
        channel.send('sudo bash agent.sh\n')
        while not channel.recv_ready():  # 명령어 실행 결과 확인
            time.sleep(0.1)
        channel.send(password + '\n')
        time.sleep(20)
        while not channel.recv_ready():
            time.sleep(0.1)
            #여기도 본인의 result.json 파일 경로 위치 지정
        sftp.get("result.json", r"C:\Spring\project\KosaProject\result.json")
        channel.send('rm agent.sh\n')
        channel.send('rm result.json\n')
        time.sleep(2)
        channel.send('y\n')

        sftp.close()
        ssh.close()
        
        jsonfile_rename()
        messagebox.showinfo("Success", "Agent works successfully!")
    except Exception as e:
        messagebox.showerror("Error", str(e))

def on_destroy():
    messagebox.showinfo("Stop", "Agent was done!")
    root.destroy()  # 이벤트 루프 종료 및 모든 Tkinter 객체 제거

def jsonfile_rename():
    # JSON 파일 경로 #여기도 본인의 result.json 파일 경로 위치 지정
    file_path = r'C:\Spring\project\KosaProject\result.json'
    # JSON 파일 읽기
    with open(file_path, 'r', encoding='utf-8') as file:
        data = json.load(file)
    # 'Server_Info' 값 가져오기
    server_info = data.get('Server_Info', 'Server_Info not found')
    # 파일 이름 변경
    os.rename(file_path, f'result_{server_info["UNIQ_ID"]}.json')


# GUI 설정
root = tk.Tk()
root.title("CCE Agent")

tk.Label(root, text="Username").grid(row=0)
tk.Label(root, text="Password").grid(row=1)
tk.Label(root, text="Port").grid(row=2)
tk.Label(root, text="IP Address").grid(row=3)

entry_username = tk.Entry(root)
entry_password = tk.Entry(root, show='*')
entry_port = tk.Entry(root)
entry_ip = tk.Entry(root)

entry_username.grid(row=0, column=1)
entry_password.grid(row=1, column=1)
entry_port.grid(row=2, column=1)
entry_ip.grid(row=3, column=1)

tk.Button(root, text="  Start  ", command=start_agent).grid(row=4, column=0)
tk.Button(root, text=" Stop ", command=on_destroy).grid(row=4, column=1, columnspan=2)

root.mainloop()