package practica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

class Token {
	String tipo;
	String valor;

	public Token(String tipo, String valor) { // formato token
		this.tipo = tipo;
		this.valor = valor;
	}
}

/*
 * class Simbolo { String lexema; HashMap<String, String> atributos;
 * 
 * public Simbolo(String lexema) { this.lexema = lexema; this.atributos = new
 * HashMap<>(); } }
 */
//

public class AnalizadorLexico {
	private static final int MAX_CARACTERES = 64; // Máximo número de caracteres permitidos
	private static final int MAX_ENTEROS = 32767; // Máximo número entero
	int contadorLineas = 1;
	private int numeroID = 0;
	private HashMap<String, Integer> idGuardados;
	private HashMap<String, String> palabrasReservadas;
	private BufferedReader lector;
	private int antescasteo;
	private char actual;
	TablaDeSimbolos TDS;
	boolean ZonaDec;
	boolean ZonaDecF;
	boolean ZonaFunc;
	String idFuncAct;
	boolean noleido = true;
	public HashMap<Integer, String> guardarLex;
	HashMap<String, Integer> idGlobalOculto;
	// private TreeMap<Integer, Simbolo> tablaDeSimbolos;
	// private int idTS = 0;
	int linea = 1;

	public AnalizadorLexico(String archivo, TablaDeSimbolos TDS) throws IOException {
		palabrasReservadas = new HashMap<>();
		idGuardados = new HashMap<>();
		palabrasReservadas.put("boolean", "BOOL");
		palabrasReservadas.put("function", "FUNC");
		palabrasReservadas.put("get", "GET");
		palabrasReservadas.put("if", "IF");
		palabrasReservadas.put("int", "INT");
		palabrasReservadas.put("let", "LET");
		palabrasReservadas.put("put", "PUT");
		palabrasReservadas.put("return", "RET");
		palabrasReservadas.put("string", "STR");
		palabrasReservadas.put("void", "VOID");
		palabrasReservadas.put("while", "WHILE");
		this.TDS = TDS;
		ZonaDec = false;
		ZonaDecF = false;
		ZonaFunc = false;
		lector = new BufferedReader(new FileReader(archivo));
		guardarLex = new HashMap<>();
		idGlobalOculto = new HashMap<>();
		// tablaDeSimbolos = new TreeMap<>();
	}

	public Token nextToken() throws IOException {
		int estado = 0;
		StringBuilder lexema = new StringBuilder();
		while (estado != -1) {
			if (noleido) {
				antescasteo = lector.read();
				if (antescasteo != -1)
					actual = (char) antescasteo;
				else if (estado != 3 && estado != 1 && estado != 10)
					estado = -1;
			} else {
				if (antescasteo == -1)
					estado = -1;
				noleido = true;
			}
			if (actual == '\r') {
				contadorLineas++;
			}

			switch (estado) {
			case -1:
				return new Token("EOF", "");
			case 0:
				while (Character.isWhitespace(actual)) {
					// Ignorar espacios en blanco
					antescasteo = lector.read();
					if (antescasteo == -1) {
						estado = -1;
						return new Token("EOF", "");
					} else {
						actual = (char) antescasteo;
						if (actual == '\r') {
							contadorLineas++;
						}
					}
				}
				if (Character.isLetter(actual)) {
					estado = 3;
					lexema.append(actual);
				} else if (Character.isDigit(actual)) {
					estado = 1;
					lexema.append(actual);
				} else {
					switch (actual) {
					case '_':
						estado = 3;
						lexema.append(actual);
						break;
					case '+':
						estado = 7;
						lexema.append(actual);
						break;
					case '=':
						return new Token("IG", "");
					case '!':
						estado = 8;
						lexema.append(actual);
						break;
					case '\'':
						estado = 10;
						lexema.append(actual);
						linea = contadorLineas;
						break;
					case ',':
						return new Token("COMA", "");
					case ';':
						return new Token("PYC", "");
					case '(':
						return new Token("APAR", "");
					case ')':
						return new Token("CPAR", "");
					case '{':
						return new Token("ACOR", "");
					case '}':
						return new Token("CCOR", "");
					case '/':
						estado = 24;
						lexema.append(actual);
						break;

					default:
						String errorMessage = "Error léxico: Carácter no válido: " + actual;
						System.err.println("Línea " + contadorLineas + ": " + errorMessage);
					}
				}
				break;

			case 1:
				// boolean error
				if (Character.isDigit(actual) && antescasteo != -1) {
					lexema.append(actual);
				} else {
					if (!Character.isWhitespace(actual))
						noleido = false;
					int valor = Integer.parseInt(lexema.toString());
					if (valor > MAX_ENTEROS) {
						lexema = new StringBuilder();
						estado = 0;
						System.err.println("Línea " + contadorLineas + ": "
								+ "Error léxico: El entero supera el valor maximo permitido.");
					}

					else
						return new Token("ENT", lexema.toString());
				}
				break;

			case 3:
				if ((Character.isLetterOrDigit(actual) || actual == '_') && antescasteo != -1) {
					lexema.append(actual);
				} else {
					if (!Character.isWhitespace(actual) || antescasteo == -1)
						noleido = false;
					String palabraReservada = palabrasReservadas.get(lexema.toString());
					if (palabraReservada != null) {
						return new Token(palabraReservada, "");
					} else if (idGuardados.isEmpty() || !idGuardados.containsKey(lexema.toString())) {
						numeroID++;
						idGuardados.put(lexema.toString(), numeroID);
						guardarLex.put(numeroID, lexema.toString());
						// System.out.println(lexema.toString());
						if (ZonaFunc) {
							idFuncAct = lexema.toString();
							TreeMap<String, ID> TDSF1 = new TreeMap<>();
							// TDSF1.put(lexema.toString(), new ID(lexema.toString()));
							TDS.TDSFunc.put(idFuncAct, TDSF1);
							TDS.TDS.put(idFuncAct, new ID(lexema.toString()));

						} else if (ZonaDecF) {
							TreeMap<String, ID> TDSF = TDS.TDSFunc.get(idFuncAct);
							TDSF.put(lexema.toString(), new ID(lexema.toString()));
							TDS.TDSFunc.put(idFuncAct, TDSF);
						} else
							TDS.TDS.put(lexema.toString(), new ID(lexema.toString()));
						return new Token("ID", Integer.toString(numeroID));
					} else {
						if (ZonaDec || ZonaFunc
								|| (ZonaDecF && TDS.TDSFunc.get(idFuncAct).containsKey(lexema.toString()))) {
							System.err.println("Línea " + contadorLineas + ": " + "Error semántico: El ID '"
									+ lexema.toString() + "' ya ha sido declarado previamente");
							System.exit(-1);
						} else if (ZonaDecF && !TDS.TDSFunc.get(idFuncAct).containsKey(lexema.toString())) {
							TreeMap<String, ID> TDSF = TDS.TDSFunc.get(idFuncAct);
							TDSF.put(lexema.toString(), new ID(lexema.toString()));
							TDS.TDSFunc.put(idFuncAct, TDSF);
							numeroID++;
							idGlobalOculto.put(lexema.toString(), idGuardados.get(lexema.toString()));
							idGuardados.put(lexema.toString(), numeroID);
							guardarLex.put(numeroID, lexema.toString());
							return new Token("ID", Integer.toString(numeroID));
						} else
							return new Token("ID", Integer.toString(idGuardados.get(lexema.toString())));
						/*
						 * else if(ZonaFunc&&ZonaDecF
						 * &&TDS.TDSFunc.get(idFuncAct).containsKey(lexema.toString())); return new
						 * Token("ID", Integer.toString(idGuardados.get(lexema.toString())));
						 */
					}

				}
				break;

			case 7:
				if (actual == '=') {
					lexema.append(actual);
					return new Token("SUMAIG", "");
				} else {
					if (!Character.isWhitespace(actual))
						noleido = false;
					return new Token("SU", "");
				}

			case 8:
				if (actual == '=') {
					lexema.append(actual);
					return new Token("DI", "");
				} else {
					if (!Character.isWhitespace(actual))
						noleido = false;
					return new Token("NOT", "");
				}

			case 10:
				if (actual == '\'') {
					lexema.append(actual);
					if (lexema.length() > MAX_CARACTERES) {
						lexema = new StringBuilder();
						estado = 0;
						System.err.println("Línea " + contadorLineas + ": "
								+ "Error léxico: La cadena supera los 64 caracteres permitidos.");
					} else if (antescasteo == -1) {
						System.err.println("Línea " + contadorLineas + ": " + "Error léxico: cadena sin cerrar.");
						return new Token("EOF", "");
					} else
						return new Token("CAD", lexema.toString());
				} else {
					if (antescasteo == -1) {
						System.err.println("Línea " + linea + ": " + "Error léxico: cadena sin cerrar.");
						return new Token("EOF", "");
					} else
						lexema.append(actual);
				}
				break;
			case 24:
				if (actual == '*') {
					actual = (char) lector.read();// Avanzar al siguiente carácter
					int ab=contadorLineas;
					while (true) {
						if (actual == '\r') {
							contadorLineas++;
						}
						if (actual == '*') {
							actual = (char) lector.read();// Avanzar al siguiente carácter
							if (actual == '/') {
								estado = 0;
								lexema = new StringBuilder();
								break; // Cierre del comentario encontrado
							}
						} else {
							// Continuar procesando otros caracteres en el comentario
							antescasteo = lector.read();
							if (antescasteo == -1/* || antescasteo == 32 */) {
								// char theChar = (char) antescasteo;

								// System.out.print(theChar);
								System.err.println(
										"Línea " + ab + ": " + "Error léxico: comentario sin cerrar.");
								return new Token("EOF", "");
							} else
								actual = (char) antescasteo;
						}
					}
				} else {
					System.err.println("Línea " + contadorLineas + ": " + "Error léxico: caracter no válido: "
							+ lexema.toString() + ".");
					estado = 0;
					if (!Character.isWhitespace(actual))
						noleido = false;
					lexema = new StringBuilder();
				}
				break;

			default:
				System.err.println(
						"Línea " + contadorLineas + ": " + "Error léxico: caracter no válido: " + actual + ".");
			}
		}
		return new Token("ERROR", "Fin de archivo inesperado");
	}

	
	/*
	 * private void añadirALaTablaDeSimbolos(Token token) { if
	 * (token.tipo.equals("ID")) { if
	 * (!tablaDeSimbolos.containsKey(Integer.parseInt(token.valor))) { Simbolo
	 * simbolo = new Simbolo(guardarLex.get(Integer.parseInt(token.valor))); // Aquí
	 * podrías agregar atributos específicos del símbolo si es necesario. // Por
	 * ejemplo: simbolo.atributos.put("Tipo", "...");
	 * tablaDeSimbolos.put(Integer.parseInt(token.valor), simbolo); } } } private
	 * void guardarTablaDeSimbolosEnFichero() throws IOException { BufferedWriter
	 * writer = new BufferedWriter(new FileWriter("tablaDeSimbolos.txt"));
	 * writer.write("CONTENIDOS DE LA TABLA PRINCIPAL #" + (++idTS) + ":\n"); for
	 * (Simbolo simbolo : tablaDeSimbolos.values()) { writer.write("* LEXEMA : '" +
	 * simbolo.lexema + "'\n"); for (String atributoKey :
	 * simbolo.atributos.keySet()) { writer.write("+ " + atributoKey + " : '" +
	 * simbolo.atributos.get(atributoKey) + "'\n"); }
	 * writer.write("--------------------\n"); } writer.close(); }
	 */

	public void restablecerGlobales(TreeMap<String, ID> ids) {
		for (Entry<String, ID> entry : ids.entrySet()) {
			if (idGlobalOculto.containsKey(entry.getKey()))
				idGuardados.put(entry.getKey(), idGlobalOculto.get(entry.getKey()));
			else
				idGuardados.remove(entry.getKey());
		}
	}

	
	/*  public static void main(String[] args) throws IOException { AnalizadorLexico
	  analizador = new AnalizadorLexico("input.txt",new TablaDeSimbolos(new TreeMap<String, ID>())); Token token; BufferedWriter
	  writer = new BufferedWriter(new FileWriter("tokens.txt")); do { token =
	  analizador.nextToken(); //analizador.añadirALaTablaDeSimbolos(token);
	  System.out.println("<" + token.tipo + "," + token.valor + ">");
	  writer.write("<" + token.tipo + "," + token.valor + ">\n"); }while
	  (!token.tipo.equals("EOF")); writer.close();
	  //analizador.guardarTablaDeSimbolosEnFichero();
	  
	  }*/
	 

}