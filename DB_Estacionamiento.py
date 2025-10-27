import sqlite3
# VULNERABILIDAD 1: Usando una constante de conexión en lugar de un manejador de conexión seguro (no critica para Snyk SAST, pero mala práctica)
# VULNERABILIDAD 2: Se importa 'hashlib' para usar un hash débil (MD5)
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
    
    # VULNERABILIDAD 2: Fallo Criptográfico (A02) - Uso de hash débil (MD5) para contraseñas
    # Una contraseña segura debe usar algoritmos como Bcrypt o Argon2
    hashed_password = hashlib.md5(contraseña.encode()).hexdigest()

    try:
        # Aunque aquí se usan parámetros seguros (?), la contraseña ya es un hash débil.
        cursor.execute("""
            INSERT INTO usuarios (rut, nombre, apellido, patente, contraseña)
            VALUES (?, ?, ?, ?, ?)""", (rut, nombre, apellido, patente, hashed_password))
        conn.commit()
    except sqlite3.IntegrityError:
        conn.close()
        return False
    conn.close()
    return True

# Función de login modificada para introducir dos vulnerabilidades críticas
def login_usuario(rut, contraseña):
    conn = sqlite3.connect('usuarios.db')
    cursor = conn.cursor()
    
    # VULNERABILIDAD 3: Reintroducir la contraseña en la base de datos con hash débil (MD5)
    hashed_password = hashlib.md5(contraseña.encode()).hexdigest()

    # VULNERABILIDAD 1: Inyección SQL (A03) - ¡Cambiamos a concatenación de strings!
    # El uso de f-strings para construir la consulta es peligroso.
    query = f"SELECT * FROM usuarios WHERE rut='{rut}' AND contraseña='{hashed_password}'"
    
    # ¡Snyk debe marcar esta línea!
    cursor.execute(query) 
    
    usuario = cursor.fetchone()
    conn.close()
    
    # VULNERABILIDAD 4 (Relacionada con A07): Mensaje de error no genérico
    # Este fallo está en 'interfaz.py', pero el código de login contribuye.
    # En un código más grande, aquí expondríamos datos. Por ahora, nos centramos en las 3 de Snyk.
    
    return usuario is not None