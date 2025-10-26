import tkinter as tk
from tkinter import messagebox
from DB_Estacionamiento import crear_tabla, registrar_usuario, login_usuario

def intentar_login():
    rut = entry_rut.get()
    password = entry_password.get()
    if login_usuario(rut, password):
        messagebox.showinfo("Éxito", "Login exitoso. ¡Bienvenido!")
    else:
        messagebox.showerror("Error", "RUT o contraseña incorrectos.")

def registrar_nuevo_usuario():
    rut = entry_reg_rut.get()
    nombre = entry_reg_nombre.get()
    apellido = entry_reg_apellido.get()
    patente = entry_reg_patente.get()
    password = entry_reg_password.get()
    exito = registrar_usuario(rut, nombre, apellido, patente, password)
    if exito:
        messagebox.showinfo("Éxito", "Usuario registrado correctamente.")
    else:
        messagebox.showerror("Error", "Ese RUT ya existe.")

crear_tabla()

root = tk.Tk()
root.title("Sistema de Estacionamiento")

# LOGIN FRAME
login_frame = tk.LabelFrame(root, text="Login")
login_frame.pack(padx=10, pady=10)

tk.Label(login_frame, text="RUT:").grid(row=0, column=0)
entry_rut = tk.Entry(login_frame)
entry_rut.grid(row=0, column=1)

tk.Label(login_frame, text="Contraseña:").grid(row=1, column=0)
entry_password = tk.Entry(login_frame, show="*")
entry_password.grid(row=1, column=1)

login_btn = tk.Button(login_frame, text="Iniciar sesión", command=intentar_login)
login_btn.grid(row=2, column=0, columnspan=2, pady=5)

# REGISTRO FRAME
registro_frame = tk.LabelFrame(root, text="Registrar nuevo usuario")
registro_frame.pack(padx=10, pady=10)

tk.Label(registro_frame, text="RUT:").grid(row=0, column=0)
entry_reg_rut = tk.Entry(registro_frame)
entry_reg_rut.grid(row=0, column=1)
tk.Label(registro_frame, text="Nombre:").grid(row=1, column=0)
entry_reg_nombre = tk.Entry(registro_frame)
entry_reg_nombre.grid(row=1, column=1)
tk.Label(registro_frame, text="Apellido:").grid(row=2, column=0)
entry_reg_apellido = tk.Entry(registro_frame)
entry_reg_apellido.grid(row=2, column=1)
tk.Label(registro_frame, text="Patente:").grid(row=3, column=0)
entry_reg_patente = tk.Entry(registro_frame)
entry_reg_patente.grid(row=3, column=1)
tk.Label(registro_frame, text="Contraseña:").grid(row=4, column=0)
entry_reg_password = tk.Entry(registro_frame, show="*")
entry_reg_password.grid(row=4, column=1)

registrar_btn = tk.Button(registro_frame, text="Registrar usuario", command=registrar_nuevo_usuario)
registrar_btn.grid(row=5, column=0, columnspan=2, pady=5)

root.mainloop()
