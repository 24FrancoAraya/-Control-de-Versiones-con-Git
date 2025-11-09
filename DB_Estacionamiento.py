import sqlite3
import hashlib

def crear_tabla():
    with sqlite3.connect('usuarios.db') as conn:
        cursor = conn.cursor()
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS usuarios (
                rut TEXT PRIMARY KEY,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                patente TEXT NOT NULL,
                contraseña TEXT NOT NULL
            )
        """)
        conn.commit()

def hash_password(password):
    # Mejor usar SHA256 en vez de MD5 para mayor seguridad
    return hashlib.sha256(password.encode()).hexdigest()

def registrar_usuario(rut, nombre, apellido, patente, contraseña):
    contraseña_hashed = hash_password(contraseña)
    try:
        with sqlite3.connect('usuarios.db') as conn:
            cursor = conn.cursor()
            cursor.execute("""
                INSERT INTO usuarios (rut, nombre, apellido, patente, contraseña)
                VALUES (?, ?, ?, ?, ?)""", (rut, nombre, apellido, patente, contraseña_hashed))
            conn.commit()
        return True
    except sqlite3.IntegrityError:
        return False

def login_usuario(rut, contraseña):
    contraseña_hashed = hash_password(contraseña)
    try:
        with sqlite3.connect('usuarios.db') as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM usuarios WHERE rut=? AND contraseña=?", (rut, contraseña_hashed))
            return cursor.fetchone() is not None
    except sqlite3.Error as e:
        print(f"Error en login: {e}")
        return False

def obtener_nombre_usuario(rut):
    try:
        with sqlite3.connect('usuarios.db') as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT nombre FROM usuarios WHERE rut=?", (rut,))
            fila = cursor.fetchone()
            if fila:
                return fila[0]
            return ""
    except sqlite3.Error as e:
        print(f"Error al obtener nombre usuario: {e}")
        return ""
