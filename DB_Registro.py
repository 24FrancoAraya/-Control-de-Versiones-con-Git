import sqlite3
from datetime import datetime, timedelta

def crear_tabla_registros():
    try:
        with sqlite3.connect('registro_estacionamiento.db') as conn:
            cursor = conn.cursor()
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS registros (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    rut_usuario TEXT NOT NULL,
                    nombre TEXT NOT NULL,
                    patente TEXT NOT NULL,
                    fecha_hora DATETIME,
                    hora_salida_estimada DATETIME,
                    estado TEXT NOT NULL
                )
            """)
            conn.commit()
    except sqlite3.Error as e:
        print(f"Error al crear tabla registros: {e}")

def registrar_entrada(rut, nombre, patente, horas_estadia):
    try:
        with sqlite3.connect('registro_estacionamiento.db') as conn:
            cursor = conn.cursor()
            # Verificar si existe registro activo
            cursor.execute("""
                SELECT COUNT(*) FROM registros
                WHERE rut_usuario=? AND estado='dentro'
            """, (rut,))
            count = cursor.fetchone()[0]
            if count > 0:
                print("Ya hay una entrada activa para este usuario.")
                return False

            ahora = datetime.now()
            salida_estim = ahora + timedelta(hours=horas_estadia)

            cursor.execute("""
                INSERT INTO registros
                (rut_usuario, nombre, patente, fecha_hora, hora_salida_estimada, estado)
                VALUES (?, ?, ?, ?, ?, 'dentro')
            """, (
                rut, nombre, patente,
                ahora.strftime('%Y-%m-%d %H:%M:%S'),
                salida_estim.strftime('%Y-%m-%d %H:%M:%S')
            ))
            conn.commit()
        return True
    except sqlite3.Error as e:
        print(f"Error al registrar entrada: {e}")
        return False

def registrar_salida(rut):
    try:
        with sqlite3.connect('registro_estacionamiento.db') as conn:
            cursor = conn.cursor()
            cursor.execute("""
                UPDATE registros
                SET estado='fuera', fecha_hora=?
                WHERE id = (
                    SELECT id FROM registros
                    WHERE rut_usuario=? AND estado='dentro'
                    ORDER BY fecha_hora DESC LIMIT 1
                )
            """, (datetime.now().strftime('%Y-%m-%d %H:%M:%S'), rut))
            conn.commit()
            return cursor.rowcount > 0
    except sqlite3.Error as e:
        print(f"Error al registrar salida: {e}")
        return False
