import sqlite3

def crear_tabla():
    conn = sqlite3.connect('usuarios.db')
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
    conn.close()

def registrar_usuario(rut, nombre, apellido, patente, contraseña):
    conn = sqlite3.connect('usuarios.db')
    cursor = conn.cursor()
    try:
        cursor.execute("""
            INSERT INTO usuarios (rut, nombre, apellido, patente, contraseña)
            VALUES (?, ?, ?, ?, ?)""", (rut, nombre, apellido, patente, contraseña))
        conn.commit()
    except sqlite3.IntegrityError:
        conn.close()
        return False
    conn.close()
    return True

def login_usuario(rut, contraseña):
    conn = sqlite3.connect('usuarios.db')
    cursor = conn.cursor()
    cursor.execute("""
        SELECT * FROM usuarios WHERE rut=? AND contraseña=?""", (rut, contraseña))
    usuario = cursor.fetchone()
    conn.close()
    return usuario is not None
