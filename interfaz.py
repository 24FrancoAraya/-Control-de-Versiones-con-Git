import tkinter as tk
from tkinter import messagebox
import sqlite3
import hashlib
import qrcode
from PIL import Image, ImageTk
from datetime import timedelta

# Importar funciones de bases de datos
from DB_Estacionamiento import crear_tabla, registrar_usuario, login_usuario, obtener_nombre_usuario
from DB_Registro import crear_tabla_registros, registrar_entrada, registrar_salida

# Función para mostrar QR
def generar_y_mostrar_qr(info):
    qr = qrcode.QRCode(version=1, box_size=10, border=4)
    qr.add_data(info)
    qr.make(fit=True)
    img = qr.make_image(fill='black', back_color='white')

    ventana_qr = tk.Toplevel(root)
    ventana_qr.title("Código QR")

    img_tk = ImageTk.PhotoImage(img)
    label_img = tk.Label(ventana_qr, image=img_tk)
    label_img.image = img_tk
    label_img.pack(padx=10, pady=10)

    boton_cerrar = tk.Button(ventana_qr, text="Cerrar", command=ventana_qr.destroy)
    boton_cerrar.pack(pady=5)

# Funciones de la interfaz

def mostrar_pantalla_bienvenida(nombre_usuario, rut_usuario, patente_usuario):
    ventana_bienvenida = tk.Toplevel(root)
    ventana_bienvenida.title("Bienvenido")
    ventana_bienvenida.geometry("350x300")
    label = tk.Label(ventana_bienvenida, text=f"Bienvenido {nombre_usuario}!", font=("Arial", 14))
    label.pack(pady=20)

    def boton_generar_qr():
        info_qr = f"Usuario: {nombre_usuario}\nRUT: {rut_usuario}\nPatente: {patente_usuario}"
        generar_y_mostrar_qr(info_qr)

    def boton_entrada():
        def validar_y_registrar():
            try:
                horas = int(entry_horas.get())
                if 1 <= horas <= 10:
                    exito = registrar_entrada(rut_usuario, nombre_usuario, patente_usuario, horas)
                    if exito:
                        messagebox.showinfo("Registro", "Entrada registrada con hora estimada de salida.")
                        ventana_horas.destroy()
                    else:
                        messagebox.showerror("Error", "Ya hay una entrada activa para este usuario.")
                else:
                    messagebox.showwarning("Advertencia", "Debe ingresar un número entre 1 y 10.")
            except ValueError:
                messagebox.showwarning("Advertencia", "Debes ingresar un número válido.")

        ventana_horas = tk.Toplevel(ventana_bienvenida)
        ventana_horas.title("Horas de estadía")

        tk.Label(ventana_horas, text="Ingrese horas que estará (1-10):").pack(pady=5)
        entry_horas = tk.Entry(ventana_horas)
        entry_horas.pack(pady=5)
        boton_aceptar = tk.Button(ventana_horas, text="Aceptar", command=validar_y_registrar)
        boton_aceptar.pack(pady=5)

    def boton_salida():
        exito = registrar_salida(rut_usuario)
        if exito:
            messagebox.showinfo("Registro", "Salida registrada correctamente.")
        else:
            messagebox.showerror("Error", "No se pudo registrar la salida o no hay entrada previa.")

    boton_qr = tk.Button(ventana_bienvenida, text="Generar código QR", command=boton_generar_qr)
    boton_qr.pack(pady=10)

    boton_registrar_entrada = tk.Button(ventana_bienvenida, text="Registrar Entrada", command=boton_entrada)
    boton_registrar_entrada.pack(pady=5)

    boton_registrar_salida = tk.Button(ventana_bienvenida, text="Registrar Salida", command=boton_salida)
    boton_registrar_salida.pack(pady=5)

def intentar_login():
    rut = entry_rut.get()
    password = entry_password.get()
    if login_usuario(rut, password):
        nombre_usuario = obtener_nombre_usuario(rut)
        # Obtener patente del usuario
        try:
            with sqlite3.connect('usuarios.db') as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT patente FROM usuarios WHERE rut=?", (rut,))
                fila = cursor.fetchone()
                patente_usuario = fila[0] if fila else ""
        except Exception as e:
            patente_usuario = ""
            print(f"Error al obtener patente: {e}")

        mostrar_pantalla_bienvenida(nombre_usuario, rut, patente_usuario)
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

# Inicializar tablas
crear_tabla()
crear_tabla_registros()

root = tk.Tk()
root.title("Sistema de Estacionamiento")

# Frame Login
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

# Frame Registro
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
