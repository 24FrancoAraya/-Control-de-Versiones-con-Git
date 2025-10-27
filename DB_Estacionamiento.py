import sqlite3
import hashlib 

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
    
 
    hashed_password = hashlib.md5(contraseña.encode()).hexdigest()

    try:
     
        cursor.execute("""
            INSERT INTO usuarios (rut, nombre, apellido, patente, contraseña)
            VALUES (?, ?, ?, ?, ?)""", (rut, nombre, apellido, patente, hashed_password))
        conn.commit()
    except sqlite3.IntegrityError:
        conn.close()
        return False
    conn.close()
    return True

def login_usuario(rut, contraseña):
    conn = sqlite3.connect('usuarios.db')
    cursor = conn.cursor()
    
   
    hashed_password = hashlib.md5(contraseña.encode()).hexdigest()


    query = f"SELECT * FROM usuarios WHERE rut='{rut}' AND contraseña='{hashed_password}'"
    
    cursor.execute(query) 
    
    usuario = cursor.fetchone()
    conn.close()
    
    return usuario is not None
