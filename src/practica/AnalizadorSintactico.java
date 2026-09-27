package practica;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class AnalizadorSintactico {
	AnalizadorLexico analizador;
	Token token;
	static String parse;
	String tok;
	boolean o106o107 = false;
	boolean cierre = false;

	/* Semantico */
	TablaDeSimbolos TDS;
	int desp;
	int despF;// desp
	TreeMap<Integer, ID> AtribFunc;
	String retEsperado;
	String enFunc;
	BufferedWriter writerTok;
	// TreeSet<ID> TDS;
	// int idTS = 0;
	// private HashMap<Integer, String> guardarLex;*/

	public AnalizadorSintactico(AnalizadorLexico analizador, TablaDeSimbolos TDS) {

		this.analizador = analizador;
		try {
			token = analizador.nextToken();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parse = "Descendente";
		tok = token.tipo;
		this.TDS = TDS;
		enFunc = "";
		desp = 0;
		despF = 0;
		try {
			writerTok = new BufferedWriter(new FileWriter("tokens.txt"));
			//writerTok.write("Listado de tokens en orden de aparicion: \n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		A();
		try {
			writerTok.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * private void añadirALaTDS(Token token) { if (token.tipo.equals("ID")) { if
	 * (!TDS.contains(Integer.parseInt(token.valor))) { //ID simbolo = new
	 * ID(/*guardarLex.get(Integer.parseInt(token.valor))); // Aquí podrías agregar
	 * atributos específicos del símbolo si es necesario. // Por ejemplo:
	 * simbolo.atributos.put("Tipo", "...");
	 * //TDS.put(Integer.parseInt(token.valor), simbolo); } } }
	 */
	/*
	 * private void guardarTDSEnFichero() throws IOException { BufferedWriter writer
	 * = new BufferedWriter(new FileWriter("TDS.txt"));
	 * writer.write("CONTENIDOS DE LA TABLA PRINCIPAL #" + (++idTS) + ":\n"); for
	 * (ID simbolo : TDS) { writer.write("* LEXEMA : '" + simbolo.lexema + "'\n");
	 * /*for (String atributoKey : simbolo.atributos.keySet()) { writer.write("+ " +
	 * atributoKey + " : '" + simbolo.atributos.get(atributoKey) + "'\n"); }
	 * writer.write("--------------------\n"); } writer.close(); }
	 */
	public void nextTok(String s) {
		try {
			token = analizador.nextToken();
			writerTok.write("<" + token.tipo + "," + token.valor + ">\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tok = token.tipo;
	}

	/*
	 * public boolean estaEnTS(ID id) { return TDS.containsKey(id.lexema); } public
	 * boolean cadOent(ID id) { if
	 * (id.tipo.equals("ent")||id.tipo.equals("cad"))return true; else if
	 * (id.tipo.equals("funcion")&&(id.TipoRetorno.equals("ent")||id.TipoRetorno.
	 * equals("cad"))) return true; return false; }
	 */
	public void decIpl(ID id) {
		id.tipo = "ent";
		id.desp = desp;
		desp+=1;
	}

	public void insertarTipoDesp(ID id, String tipo) {
		id.tipo = tipo;
		if (tipo.equals("cad")) {
			id.desp = desp;
			desp+=64;
		} else if (tipo.equals("ent")) {
			id.desp = desp;
			desp+=1;
		} else if (tipo.equals("log")) {
			id.desp = desp;
			desp+=1;
		}
	}

	public String getLex(String posTS) {// Devuelve el lexema del id
		return analizador.guardarLex.get(Integer.parseInt(token.valor));
	}

	public void RellenarParamIDyTDSFunc(TreeMap<Integer, Atributo> Atrib, ID id) {
		id.numParam = Atrib.size();
		int i = 0;
		TreeMap<String, ID> map = new TreeMap<>();
		while (id.numParam > i) {
			ID idAt = new ID(Atrib.get(i).lexAt);
			insertarTipoDesp(idAt, Atrib.get(i).tipoAt);
			map.put(Atrib.get(i).lexAt, idAt);
			i++;
		}
		TDS.TDSFunc.put(id.lexema, map);

		/*
		 * int i=0; int despF=0; while(i<Atrib.size()) { id.TipoParam.put(i,
		 * Atrib.get(i).tipoAt); id.ModoParam.put(i, 1); ID idAtr=new
		 * ID(Atrib.get(i).lexAt,Atrib.get(i).tipoAt); AtribFunc.put(i+1, idAtr); i++; }
		 */
	}

	public void A() {
		if (tok.equals("LET")) {///// SEMANTICO HECHO
			analizador.ZonaDec = true;
			nextTok("LET");
			parse = parse + " 1";
			if (tok.equals("ID")) {
				// Semantico
				ID id = TDS.TDS.get(getLex(token.valor));
				// if(id.tipo!=null) { System.err.print("error: El ID "+ getLex(token.valor)+"
				// ya ha sido declarado " + analizador.contadorLineas);return;}
				nextTok("ID");
				String tipo = B();
				insertarTipoDesp(id, tipo);// insertamos el tipo en la TS
				TDS.TDS.put(id.lexema, id);
				if (tok.equals("PYC")) {
					analizador.ZonaDec = false;
					nextTok("PYC");
					// parse = parse + " 1";
					A();
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("PUT")) {// SEMANTICA HECHA
			nextTok("PUT");
			parse = parse + " 2";
			String tipo = C();
			// Semantico
			if (!tipo.equals("ent") && !tipo.equals("cad")) {
				System.err.print(
						"Error Semantico: el ID deberia ser de tipo enetro o cadena " + analizador.contadorLineas);/// SI
				
				System.exit(-1);
			}
			// Fin Semantico
			if (tok.equals("PYC")) {
				nextTok("PYC");
				// parse = parse + " 2";
				A();
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("GET")) {// SEMANTICA HECHA
			nextTok("GET");
			parse = parse + " 3";
			if (tok.equals("ID")) {
				ID id = TDS.TDS.get(getLex(token.valor));
				if (TDS.TDS.get(id.lexema).tipo == null) {
					decIpl(id);
					TDS.TDS.put(id.lexema, id);
				}
				String tipo = id.tipo;
				if (tipo.equals("funcion"))
					tipo = id.TipoRetorno;
				if (!tipo.equals("ent") && !tipo.equals("cad")) {
					System.err.print(
							"Error Semantico: el ID deberia ser de tipo enetro o cadena " + analizador.contadorLineas);/// SI
					
					System.exit(-1);
				}
				nextTok("ID");
				// parse = parse + " 3";
				if (tok.equals("PYC")) {
					nextTok("PYC");
					// parse = parse + " 3";
					A();
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("IF")) {
			nextTok("IF");
			parse = parse + " 4";
			if (tok.equals("APAR")) {
				nextTok("APAR");
				// parse = parse + " 4";
				String tipo = D();
				if (!tipo.equals("log")) {
					System.err.print("Error Semantico: la condition del if ha de ser de tipo logico. Linea: "
							+ analizador.contadorLineas);
					System.exit(-1);
				}
				if (tok.equals("CPAR")) {
					nextTok("CPAR");
					// parse = parse + " 4";
					E();
					if (tok.equals("PYC")) {
						nextTok("PYC");
						// parse = parse + " 4";
						A();
					} else {
						System.err
								.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
						System.exit(-1);
					}
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("WHILE")) {
			nextTok("WHILE");
			parse = parse + " 5";
			if (tok.equals("APAR")) {
				nextTok("APAR");
				// parse = parse + " 5";
				String tipo = D();
				if (!tipo.equals("log")) {
					System.err.print("Error Semantico: la condition del while ha de ser de tipo logico. Linea: "
							+ analizador.contadorLineas);
					System.exit(-1);
				}
				if (tok.equals("CPAR")) {
					nextTok("CPAR");
					// parse = parse + " 5";
					if (tok.equals("ACOR")) {
						nextTok("ACOR");
						// parse = parse + " 5";
						F();
						if (tok.equals("CCOR")) {
							nextTok("CCOR");
							// parse = parse + " 5";
							A();
						} else {
							System.err.print(
									"Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
							System.exit(-1);
						}
					} else {
						System.err
								.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
						System.exit(-1);
					}
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("FUNC")) {
			// analizador.ZonaDec=true;
			analizador.ZonaFunc = true;
			nextTok("FUNC");
			parse = parse + " 6";
			if (tok.equals("ID")) {
				// analizador.ZonaDec=false;//YA HEMOS DECLARADO EL ID DE LA FUNCION QUE DEBE
				// ESTAR EN LA TDS POR TANTO CERRAMOS ZONADEC
				analizador.ZonaFunc = false;
				ID id = TDS.TDS.get(getLex(token.valor));
				// analizador.idFuncAct=id.lexema;
				// if(id.tipo!=null) { System.err.print("error: El ID "+ getLex(token.valor)+"
				// ya ha sido declarado " + analizador.contadorLineas);return;}
				id.tipo = "funcion";
				// TDS.TDS.put(id.lexema,id);
				nextTok("ID");
				String tipoG = G();
				id.TipoRetorno = tipoG;
				retEsperado = tipoG;// para comprobar que el ret esta bien semanticamente, necesario porque no
									// podemos rescatar el id de la funcion para sacar id.tiporet cuando el ret se
									// hace en un if
				if (tok.equals("APAR")) {
					analizador.ZonaDecF = true;// ACTIVAMOS ZONADECF PARA METER LOS ATRRIBUTOS EN LA TDSFUNC NO EN TDS
					nextTok("APAR");
					// parse = parse + " 6";
					TreeMap<Integer, Atributo> Atrib = H();
					id.Atributos = Atrib;
					TDS.TDS.put(id.lexema, id);
					RellenarParamIDyTDSFunc(Atrib, id);
					analizador.ZonaDecF = false;// YA METIMOS TODOS LOS ID EN TDSFUNC POR TANTO CERRAMOS LA ZONADECFUNC
					if (tok.equals("CPAR")) {
						nextTok("CPAR");
						// parse = parse + " 6";
						if (tok.equals("ACOR")) {
							nextTok("ACOR");
							enFunc = id.lexema;
							// parse = parse + " 6";
							F();
							if (tok.equals("CCOR")) {
								//if(retEsperado.equals(void))
								retEsperado=null;
								analizador.restablecerGlobales(TDS.TDSFunc.get(enFunc));
								enFunc = "";
								nextTok("CCOR");
								// despF=0;
								// Vaciar TDSFunc y AtribFunc
								// meterlo en fichero
								// parse = parse + " 6";
								A();
							} else {
								System.err.print(
										"Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
								System.exit(-1);
							}
						} else {
							System.err.print(
									"Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
							System.exit(-1);
						}
					} else {
						System.err
								.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
						System.exit(-1);
					}
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("ID")) {
			ID id = TDS.TDS.get(getLex(token.valor));
			
			if (id.tipo == null) {
				decIpl(id);
				TDS.TDS.put(id.lexema, id);
			}
			String idTipo = id.tipo;
			if (idTipo.equals("funcion"))
				idTipo = id.TipoRetorno;
			nextTok("ID");
			parse = parse + " 7";
			String tipoI = I(id);
			if(!tipoI.equals("tipo_ok")) {
			if (tipoI.equals("errorS") || (tipoI.equals("entS") && !idTipo.equals("ent"))) {
				System.err.print(
						"Error Semantico: Asignacion incorrecta, uso de el operador de asignacion '+=' con elementos no enteros . Linea: "
								+ analizador.contadorLineas);
				System.exit(-1);
			}
			if(tipoI.equals("entS")) tipoI="ent";
			if (tipoI.equals("error") || !idTipo.equals(tipoI)) {
				System.err.print("Error Semantico: Asignacion incorrecta, se esperaba " + idTipo + " . Linea: "
						+ analizador.contadorLineas);
				System.exit(-1);
			}}
			if (tok.equals("PYC")) {
				nextTok("PYC");
				// parse = parse + " 7";
				A();
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("EOF")) {
			// nextTok("EOF");
			parse = parse + " 8";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
	}

	public String B() {
		if (tok.equals("STR")) {
			// id.desp=desp+64; id.tipo="cad";
			nextTok("STR");
			parse = parse + " 9";
			return "cad";
		} else if (tok.equals("INT")) {
			// id.desp=desp+1; id.tipo="ent";
			nextTok("INT");
			parse = parse + " 10";
			return "ent";
		} else if (tok.equals("BOOL")) {
			// id.desp=desp+1; id.tipo="log";
			nextTok("BOOL");
			parse = parse + " 11";
			return "log";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String C() {
		if (tok.equals("ID") || tok.equals("ENT")) {
			parse = parse + " 12";
			String tipo = X();
			String tipoJ = J();
			if (tipoJ.equals("vacio") ||(tipo.equals("ent")&& tipo.equals(tipoJ)))
				return tipo;
			else {
				System.err.print(
						"Error Semantico: los sumandos deben de ser enteros. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			return tipo;
		} else if (tok.equals("CAD")) {
			nextTok("CAD");
			parse = parse + " 13";
			return "cad";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String D() {
		if (tok.equals("ID") || tok.equals("ENT")) {
			parse = parse + " 14";
			String idTipo = X();
			String tipoJ = J();
			if (!tipoJ.equals("vacio") &&(!idTipo.equals("ent") || !idTipo.equals(tipoJ))) {		
				System.err.print(
						"Error Semantico: los sumandos deben de ser enteros. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}

			String tipo = D2();
			if ((tipo.equals("ent") || tipo.equals("log")) && tipo.equals(idTipo))
				return "log";
			else if (tipo.equals("vacio") && idTipo.equals("log"))
				return "log";
			else if (tipo.equals("vacio") && idTipo.equals("ent"))
				return "ent";
			else if (tipo.equals("vacio") && idTipo.equals("cad"))
				return "cad";
			else return "error";
		} else if (tok.equals("NOT")) {
			parse = parse + " 15";
			nextTok("NOT");
			String tipo = D1();
			return tipo;
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String D1() {
		if (tok.equals("ID")) {
			parse = parse + " 16";
			ID id = new ID(getLex(token.valor));
			if (!enFunc.equals(""))
				id = TDS.TDSFunc.get(enFunc).get(getLex(token.valor));
			if (id == null || id.tipo == null)
				id = TDS.TDS.get(getLex(token.valor));
			if (id.tipo == null) {
				decIpl(id);
				TDS.TDS.put(id.lexema, id);
			}
			String idTipo = id.tipo;
			if (idTipo.equals("funcion"))
				idTipo = id.TipoRetorno;
			nextTok("ID");
			M(id);
			if (idTipo.equals("log"))
				return "log";
			else {
				System.err.print(
						"Error Semantico: uso incorrecto del operador lógico '!' con un elemento no booleano. Linea: " + analizador.contadorLineas);
				System.exit(-1);}
		} else if (tok.equals("APAR")) {
			parse = parse + " 17";
			nextTok("APAR");
			if (tok.equals("ID") || tok.equals("ENT")) {
				//parse = parse + " 14";
				String idTipo = X();
				String tipoJ = J();
				if (!tipoJ.equals("vacio") &&(!idTipo.equals("ent")|| !idTipo.equals(tipoJ))) {
					System.err.print(
							"Error Semantico: los sumandos deben de ser enteros. Linea: " + analizador.contadorLineas);
					System.exit(-1);
				}
				String tipo = D2();

				if (tok.equals("CPAR")) {
					//parse = parse + " 17";
					nextTok("CPAR");
					if ((tipo.equals("ent") || tipo.equals("log")) && tipo.equals(idTipo))
						return "log";
					else if (tipo.equals("vacio") && idTipo.equals("log"))
						return "log";
//					else if (tipo.equals("vacio") && idTipo.equals("ent"))
//						return "ent";
					else if(!tipo.equals("vacio")){
						System.err.print(
								"Error Semantico: uso incorrecto del operador de relacion '!='. Linea: " + analizador.contadorLineas);
						System.exit(-1);
					}
					else {
						System.err.print(
								"Error Semantico: uso incorrecto del operador lógico '!' con un elemento no booleano. Linea: " + analizador.contadorLineas);
						System.exit(-1);
					}
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String D2() {
		if (tok.equals("DI")) {
			parse = parse + " 18";
			nextTok("DI");
			String tipo = X();
			String tipoJ = J();
			if (tipoJ.equals("vacio") ||(tipo.equals("ent")&& tipo.equals(tipoJ)))	

				return tipo;
			else {
				System.err.print(
						"Error Semantico: los sumandos deben de ser enteros. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			return tipo;
		} else if (tok.equals("CPAR") || tok.equals("PYC")) {
			parse = parse + " 19";
			return "vacio";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String E() {
		if (tok.equals("ID")) {
			ID id = new ID(getLex(token.valor));
			// String l=getLex(token.valor);
			if (!enFunc.equals(""))
				id = TDS.TDSFunc.get(enFunc).get(getLex(token.valor));
			if (id == null || id.tipo == null)
				id = TDS.TDS.get(getLex(token.valor));
			// ID id=TDS.TDS.get(getLex(token.valor)) ;
			if (id.tipo == null) {
				decIpl(id);
				TDS.TDS.put(id.lexema, id);
			}
			String idTipo = id.tipo;
			if (idTipo.equals("funcion"))
				idTipo = id.TipoRetorno;
			nextTok("ID");
			parse = parse + " 20";
			String tipoI = I(id);
			if(!tipoI.equals("tipo_ok")){
			if (tipoI.equals("errorS") || (tipoI.equals("entS") && !idTipo.equals("ent"))) {
				System.err.print(
						"Error Semantico: Asignacion incorrecta, uso de el operador de asignacion '+=' con elementos no enteros . Linea: "
								+ analizador.contadorLineas);
				System.exit(-1);
			}
			if(tipoI.equals("entS")) tipoI="ent";
			if (!tipoI.equals("error") && idTipo.equals(tipoI))
				return "tipo_ok";
			else {
				System.err.print("Error Semantico: Asignacion incorrecta, se esperaba " + idTipo + " . Linea: "
						+ analizador.contadorLineas);
				System.exit(-1);
			}}
		} else if (tok.equals("PUT")) {
			nextTok("PUT");
			parse = parse + " 21";
			String tipoC = C();
			if (!tipoC.equals("ent") && !tipoC.equals("cad")) {
				System.err.print(
						"Error Semantico: el ID deberia ser de tipo enetro o cadena " + analizador.contadorLineas);
				System.exit(-1);
			} else
				return "tipo_ok";

		} else if (tok.equals("GET")) {
			nextTok("GET");
			parse = parse + " 22";
			if (tok.equals("ID")) {
				ID id = TDS.TDS.get(getLex(token.valor));
				if (TDS.TDS.get(id.lexema).tipo == null) {
					decIpl(id);
					TDS.TDS.put(id.lexema, id);
				}
				String tipo = id.tipo;
				if (tipo.equals("funcion"))
					tipo = id.TipoRetorno;
				nextTok("ID");
				if (!tipo.equals("ent") && !tipo.equals("cad")) {
					System.err.print(
							"Error Semantico: el ID deberia ser de tipo enetro o cadena " + analizador.contadorLineas);
					System.exit(-1);
				} else
					return "tipo_ok";
				// parse = parse + " 22";
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}

		} else if (tok.equals("RET")) {
			
			if (retEsperado == null) {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
			parse = parse + " 23";
			nextTok("RET");
			String tipoE1 = E1();
			if(!retEsperado.equals(tipoE1)) {
				System.err.print("Error Semantico: la funcion deberia retornar " + retEsperado + " en lugar de "
						+ tipoE1 + ". Linea: " + analizador.contadorLineas);
				System.exit(-1);
			} else {
				retEsperado = null;
				return "tipo_ok";
			}
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String E1() {
		if (tok.equals("CAD")) {
			nextTok("CAD");
			parse = parse + " 24";
			return "cad";
		} else if (tok.equals("ID") || tok.equals("ENT")||tok.equals("NOT")) {
			parse = parse + " 25";
			return D();
		}
		else if(tok.equals("PYC")) {
			parse = parse + " 26";
			return "void";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public void F() {
		if (tok.equals("ID")) {
			ID id = new ID(getLex(token.valor));
			// String l=getLex(token.valor);
			if (!enFunc.equals(""))
				id = TDS.TDSFunc.get(enFunc).get(getLex(token.valor));
			if (id == null || id.tipo == null)
				id = TDS.TDS.get(getLex(token.valor));
			// ID id=TDS.TDS.get(getLex(token.valor)) ;
			if (id.tipo == null) {
				decIpl(id);
				TDS.TDS.put(id.lexema, id);
			}
			String idTipo = id.tipo;
			if (idTipo.equals("funcion"))
				idTipo = id.TipoRetorno;
			nextTok("ID");
			parse = parse + " 27";
			// I(id);
			String tipoI = I(id);
			if(!tipoI.equals("tipo_ok")) {
			if (tipoI.equals("errorS") || (tipoI.equals("entS") && !idTipo.equals("ent"))) {
				System.err.print(
						"Error Semantico: Asignacion incorrecta, uso de el operador de asignacion '+=' con elementos no enteros . Linea: "
								+ analizador.contadorLineas);
				System.exit(-1);
			}
			if(tipoI.equals("entS")) tipoI="ent";
			if (tipoI.equals("error") || !idTipo.equals(tipoI)) {
				System.err.print("Error Semantico: Asignacion incorrecta, se esperaba " + idTipo + ". Linea: "
						+ analizador.contadorLineas);
				System.exit(-1);
			}}
			if (tok.equals("PYC")) {
				nextTok("PYC");
				F();
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("LET")) {
			if (!enFunc.equals(""))
				analizador.ZonaDecF = true;
			else
				analizador.ZonaDec = true;
			nextTok("LET");
			parse = parse + " 28";
			if (tok.equals("ID")) {
				ID id = new ID(getLex(token.valor));
				if (analizador.ZonaDecF) {
					// analizador.ZonaDecF =false;
					id = TDS.TDSFunc.get(enFunc).get(getLex(token.valor));
				} else if (analizador.ZonaDec) {
					// analizador.ZonaDec=false;
					id = TDS.TDS.get(getLex(token.valor));
				}
				nextTok("ID");
				String tipo = B();
				if (analizador.ZonaDecF) {
					insertarTipoDesp(id, tipo);// insertamos el tipo en la TS
					// TDS.TDSFunc.put(id.lexema, id);
					TDS.addIdTDSFunc(enFunc, id);
					analizador.ZonaDecF = false;
				} else if (analizador.ZonaDec) {
					insertarTipoDesp(id, tipo);
					TDS.TDS.put(id.lexema, id);
					analizador.ZonaDec = false;
				}
				if (tok.equals("PYC")) {
					nextTok("PYC");
					F();
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);

			}
		} else if (tok.equals("PUT")) {
			nextTok("PUT");
			parse = parse + " 29";
			String tipoC = C();
			if (!tipoC.equals("ent") && !tipoC.equals("cad")) {
				System.err.print(
						"Error Semantico: el ID deberia ser de tipo enetro o cadena " + analizador.contadorLineas);
				System.exit(-1);
			}
			if (tok.equals("PYC")) {
				nextTok("PYC");
				F();
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("GET")) {
			nextTok("GET");
			parse = parse + " 30";
			if (tok.equals("ID")) {
				ID id = new ID(getLex(token.valor));
				// String l=getLex(token.valor);
				if (!enFunc.equals(""))
					id = TDS.TDSFunc.get(enFunc).get(getLex(token.valor));
				if (id == null || id.tipo == null)
					id = TDS.TDS.get(getLex(token.valor));
				// ID id=TDS.TDS.get(getLex(token.valor)) ;
				if (id.tipo == null) {
					decIpl(id);
					TDS.TDS.put(id.lexema, id);
				}
				String idTipo = id.tipo;
				if (idTipo.equals("funcion"))
					idTipo = id.TipoRetorno;
				if (!idTipo.equals("ent") && !idTipo.equals("cad")) {
					System.err.print(
							"Error Semantico: el ID deberia ser de tipo enetro o cadena " + analizador.contadorLineas);
					System.exit(-1);
				}
				nextTok("ID");

				if (tok.equals("PYC")) {
					nextTok("PYC");
					F();
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("IF")) {
			nextTok("IF");
			parse = parse + " 31";
			if (tok.equals("APAR")) {
				nextTok("APAR");
				// parse = parse + " 4";
				String tipo = D();
				if (!tipo.equals("log")) {
					System.err.print("Error Semantico: la condition del if ha de ser de tipo logico. Linea: "
							+ analizador.contadorLineas);
					System.exit(-1);
				}
				if (tok.equals("CPAR")) {
					nextTok("CPAR");
					// parse = parse + " 4";
					E();
					if (tok.equals("PYC")) {
						nextTok("PYC");
						// parse = parse + " 4";
						F();
					} else {
						System.err
								.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
						System.exit(-1);
					}
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("WHILE")) {
			nextTok("WHILE");
			parse = parse + " 32";
			if (tok.equals("APAR")) {
				nextTok("APAR");
				// parse = parse + " 5";
				String tipo = D();
				if (!tipo.equals("log")) {
					System.err.print("Error Semantico: la condition del while ha de ser de tipo logico. Linea: "
							+ analizador.contadorLineas);
					System.exit(-1);
				}
				if (tok.equals("CPAR")) {
					nextTok("CPAR");
					// parse = parse + " 5";
					if (tok.equals("ACOR")) {
						nextTok("ACOR");
						// parse = parse + " 5";
						F();
						if (tok.equals("CCOR")) {
							nextTok("CCOR");
							// parse = parse + " 5";
							F();
						} else {
							System.err.print(
									"Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
							System.exit(-1);
						}
					} else {
						System.err
								.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
						System.exit(-1);
					}
				} else {
					System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
					System.exit(-1);
				}
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("RET")) {
			
			if (retEsperado == null) {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}			
			nextTok("RET");
			parse = parse + " 33";
			String tipoE1 = E1();
			if(!retEsperado.equals(tipoE1)) {
				System.err.print("Error Semantico: la funcion deberia retornar " + retEsperado + " en lugar de "
						+ tipoE1 + ". Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			//retEsperado = null;
			// else return "tipo_ok";
			if (tok.equals("PYC")) {
				nextTok("PYC");
				F();
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("CCOR")) {
			parse = parse + " 34";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
	}

	public String G() {
		if (tok.equals("INT")) {
			nextTok("INT");
			parse = parse + " 35";
			return "ent";
		} else if (tok.equals("BOOL")) {
			nextTok("BOOL");
			parse = parse + " 36";
			return "log";
		} else if (tok.equals("STR")) {
			nextTok("STR");
			parse = parse + " 37";
			return "cad";
		} else if (tok.equals("VOID")) {
			nextTok("VOID");
			parse = parse + " 38";
			return "void";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public TreeMap<Integer, Atributo> H() {
		int posAt = 0;
		TreeMap<Integer, Atributo> Atrib = new TreeMap<>();
		if (tok.equals("VOID")) {
			nextTok("VOID");
			parse = parse + " 39";
			return Atrib;
		} else if (tok.equals("INT")) {
			// String tipoAtr="INT";
			nextTok("INT");
			parse = parse + " 40";
			if (tok.equals("ID")) {
				// FALTA VER COMO ES EL TIPO PARAM
				Atrib.put(posAt, new Atributo(getLex(token.valor), "ent", despF += 1));
				posAt++;
				nextTok("ID");
				H1(Atrib);
				return Atrib;
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("BOOL")) {
			// String tipoAtr="BOOL";
			nextTok("BOOL");
			parse = parse + " 41";
			if (tok.equals("ID")) {
				Atrib.put(posAt, new Atributo(getLex(token.valor), "log", despF += 1));
				posAt++;
				nextTok("ID");
				H1(Atrib);
				return Atrib;
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("STR")) {
			// String tipoAtr="STR";
			nextTok("STR");
			parse = parse + " 42";
			if (tok.equals("ID")) {
				Atrib.put(posAt, new Atributo(getLex(token.valor), "cad", despF += 64));
				posAt++;
				nextTok("ID");
				H1(Atrib);
				return Atrib;
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return null;
	}

	public void H1(TreeMap<Integer, Atributo> Atrib) {
		if (tok.equals("COMA")) {
			nextTok("COMA");
			parse = parse + " 43";
			H2(Atrib);
			H1(Atrib);
		} else if (tok.equals("CPAR")) {
			parse = parse + " 44";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
	}

	public void H2(TreeMap<Integer, Atributo> Atrib) {
		int posAt = Atrib.size();
		if (tok.equals("INT")) {
			// String tipoAtr="INT";
			nextTok("INT");
			parse = parse + " 45";
			if (tok.equals("ID")) {
				Atrib.put(posAt, new Atributo(getLex(token.valor), "ent", despF += 1));
				posAt++;
				nextTok("ID");
				// parse = parse + " 33";
				//H1(Atrib);
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("BOOL")) {
			// String tipoAtr="BOOL";
			nextTok("BOOL");
			parse = parse + " 46";
			if (tok.equals("ID")) {
				Atrib.put(posAt, new Atributo(getLex(token.valor), "log", despF += 1));
				posAt++;
				nextTok("ID");
				// parse = parse + " 33";
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("STR")) {
			// String tipoAtr="STR";
			nextTok("STR");
			parse = parse + " 47";
			if (tok.equals("ID")) {
				Atrib.put(posAt, new Atributo(getLex(token.valor), "cad", despF += 64));
				posAt++;
				nextTok("ID");
				// parse = parse + " 33";
				//H1(Atrib);
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
	}

	public String I(ID id) {
		if (tok.equals("APAR")) {
			nextTok("APAR");
			parse = parse + " 48";
			K(id);
			if (tok.equals("CPAR")) {
				nextTok("CPAR");
				return "tipo_ok";
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("IG") || tok.equals("PYC")) {
			parse = parse + " 49";
			String tipoL = L();
			if (tipoL.equals("vacio"))
				return "tipo_ok";
			else
				return tipoL;
		} else if (tok.equals("SUMAIG")) {
			nextTok("SUMAIG");
			parse = parse + " 50";
			String tipoX = X();
			if (!tipoX.equals("ent")) {
				return "errorS";
			}
			return "entS";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String J() {
		if (tok.equals("SU")) {
			nextTok("SU");
			parse = parse + " 51";
			String tipoX = X();
			String tipoJ = J();
			if (tipoJ.equals("vacio") || (tipoX.equals("ent") && tipoX.equals(tipoJ)))
				return tipoX;
			else {
				System.err.print(
						"Error Semantico: los sumandos deben de ser enteros. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("COMA") || tok.equals("CPAR") || tok.equals("PYC") || tok.equals("DI")) {
			parse = parse + " 52";
			return "vacio";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public void K(ID id) {
		if (!id.tipo.equals("funcion")) {
			System.err.print(
					"Error Semantico: El id " + id.lexema + " no es una funcion. Linea: " + analizador.contadorLineas);
			System.exit(-1);
		}
		int posAtr = 0;
		if (tok.equals("CPAR")) {
			parse = parse + " 53";
			if (!id.Atributos.isEmpty()) {
				System.err.print("Error Semantico: La funcion " + id.lexema + " debe ser llamada con "
						+ id.Atributos.size() + " atributos. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
		} else if (tok.equals("CAD")) {
			nextTok("CAD");
			parse = parse + " 54";
			if (id.Atributos.isEmpty()) {
				System.err.print("Error Semantico: La funcion " + id.lexema + " debe ser llamada con "
						+ id.Atributos.size() + " atributos. Linea: " + analizador.contadorLineas);
				System.exit(-1);

			} else if (!id.Atributos.get(posAtr).tipoAt.equals("cad")) {
				System.err.print(
						"Error Semantico: Llamada a funcion erronea, se esperaba " + id.Atributos.get(posAtr).tipoAt
								+ " en lugar de una cadena. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			posAtr++;
			K1(id, posAtr);
		} else if (tok.equals("ID") || tok.equals("ENT")) {
			parse = parse + " 55";
			String tipoX = X();
			String tipoJ = J();
			if (!tipoJ.equals("vacio") &&(!tipoX.equals("ent") || !tipoX.equals(tipoJ))) {		

				System.err.print(
						"Error Semantico: los sumandos deben de ser enteros. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			if (id.Atributos.isEmpty()) {
				System.err.print("Error Semantico: La funcion " + id.lexema + " debe ser llamada con "
						+ id.Atributos.size() + " atributos. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			} else if (!id.Atributos.get(posAtr).tipoAt.equals(tipoX)) {
				System.err.print(
						"Error semantico: Llamada a funcion erronea, se esperaba " + id.Atributos.get(posAtr).tipoAt
								+ " en lugar de " + tipoX + ". Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			posAtr++;
			K1(id, posAtr);
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
	}

	public void K1(ID id, int posAtr) {
		if (tok.equals("COMA")) {
			nextTok("COMA");
			parse = parse + " 56";
			int posAtr2 = K2(id, posAtr);
			K1(id, posAtr2);
		} else if (tok.equals("CPAR")) {
			parse = parse + " 57";
			if (id.Atributos.size() > posAtr) {
				System.err.print("Error Semantico: La funcion " + id.lexema + " debe ser llamada con "
						+ id.Atributos.size() + " atributos. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
	}

	public int K2(ID id, int posAtr) {
		if (tok.equals("CAD")) {
			nextTok("CAD");
			parse = parse + " 58";
			if (id.Atributos.size() <= posAtr) {
				System.err.print("Error Semantico: La funcion " + id.lexema + " debe ser llamada con "
						+ id.Atributos.size() + " atributos. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			} else if (!id.Atributos.get(posAtr).tipoAt.equals("cad")) {
				System.err.print(
						"Error Semantico: Llamada a funcion erronea, se esperaba " + id.Atributos.get(posAtr).tipoAt
								+ " en lugar de una cadena. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			posAtr++;
			return posAtr;
		} else if (tok.equals("ID") || tok.equals("ENT")) {
			parse = parse + " 59";
			String tipoX = X();
			String tipoJ = J();
			if (!tipoJ.equals("vacio") &&(!tipoX.equals("ent") || !tipoX.equals(tipoJ))) {		
				System.err.print(
						"Error Semantico: los sumandos deben de ser enteros. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			if (id.Atributos.size() <= posAtr) {
				System.err.print("Error Semantico: La funcion " + id.lexema + " debe ser llamada con "
						+ id.Atributos.size() + " atributos. Linea: " + analizador.contadorLineas);
				System.exit(-1);
			} else if (!id.Atributos.get(posAtr).tipoAt.equals(tipoX)) {
				System.err.print(
						"Error Semantico: Llamada a funcion erronea, se esperaba " + id.Atributos.get(posAtr).tipoAt
								+ " en lugar de " + tipoX + ". Linea: " + analizador.contadorLineas);
				System.exit(-1);
			}
			posAtr++;
			return posAtr;
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
			return 0;
		}
	}

	public String L() {
		if (tok.equals("IG")) {
			nextTok("IG");
			parse = parse + " 60";
			return L1();
		} else if (tok.equals("PYC")) {
			parse = parse + " 61";
			return "vacio";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String L1() {
		if (tok.equals("CAD")) {
			nextTok("CAD");
			parse = parse + " 62";
			return "cad";
		} else if (tok.equals("ID") || tok.equals("ENT") || tok.equals("NOT")) {
			parse = parse + " 63";
			String tipoD = D();
			return tipoD;
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public String X() {
		if (tok.equals("ID")) {
			ID id = null;
			if (!enFunc.equals(""))
				id = TDS.TDSFunc.get(enFunc).get(getLex(token.valor));
			if (id == null)
				id = TDS.TDS.get(getLex(token.valor));
			if (id.tipo == null) {
				decIpl(id);
				TDS.TDS.put(id.lexema, id);
			}
			String tipo = id.tipo;
			if (tipo.equals("funcion"))
				tipo = id.TipoRetorno;
			nextTok("ID");
			parse = parse + " 64";
			M(id);
			return tipo;
		} else if (tok.equals("ENT")) {
			nextTok("ENT");
			parse = parse + " 65";
			return "ent";
		} else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);
		}
		return "error";
	}

	public void M(ID id) {
		if (tok.equals("APAR")) {
			nextTok("APAR");
			parse = parse + " 66";
			K(id);
			if (tok.equals("CPAR")) {
				nextTok("CPAR");
			} else {
				System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
				System.exit(-1);

			}
		} else if (tok.equals("PYC") || tok.equals("CPAR") || tok.equals("DI")||tok.equals("SU")||tok.equals("COMA"))
			parse = parse + " 67";
		else {
			System.err.print("Error Sintactico: token " + tok + " inesperado. Linea:" + analizador.contadorLineas);
			System.exit(-1);

		}
	}

public static void meteEnFichero(TablaDeSimbolos TDS, String filename) {
		
		try (FileWriter writer = new FileWriter(filename)) {
			int numTabla = 0;
			writer.write("TABLA PRINCIPAL #1:\n\n");
            for (Map.Entry<String, ID> entry : TDS.TDS.entrySet()) {
           // numTabla++;
           // writer.write("TABLA PRINCIPAL #" + numTabla + "\n");
            if(entry.getValue().tipo.equals("funcion")) {
            	
          //  	String treeMapKey = entry.getKey();
                Map<Integer, Atributo> nestedMap = entry.getValue().Atributos;
            
            	writer.write("* '" + entry.getKey() + "'\n" + " + EtiqFuncion: 'Et"+entry.getKey()+"'\n"
            			+ " + tipoRetorno: '" + entry.getValue().TipoRetorno + "'\n"
            			/*+ " + lexema:" + entry.getValue().lexema + "\n"*/
            			+ " + tipo: '" + entry.getValue().tipo + "'\n"
            			+ " + numParam: " + entry.getValue().numParam + "\n"/*
            			+ " + desp:" + entry.getValue().desp + "\n"*/
            			);
            	if(!nestedMap.isEmpty()) writer.write("  ATRIBUTOS:\n");
            	for (Map.Entry<Integer, Atributo> nestedEntry : nestedMap.entrySet()) {
            		numTabla++;
            		writer.write(/*"  ATRIBUTO #" + numTabla + ":\n" /*+ "  + lexema: " + nestedEntry.getValue().lexAt + "\n"*/
            				"  + TipoParam"+numTabla+": '" + nestedEntry.getValue().tipoAt + "'\n"
            				+ "  + ModoParam"+numTabla+": 1\n"/*
            				+ "  + desp:" + nestedEntry.getValue().desp + "\n"*/
            				/*+ "  + tipoParametro:" + nestedEntry.getValue().tipoParam + "\n"*/
            				);
            	}
            	numTabla=0;
            } else {
                writer.write(                		
                		"* '" + entry.getKey() + "'\n" /*+ " + lexema:" + entry.getValue().lexema + "\n"*/
                				+ " + tipo: '" + entry.getValue().tipo + "'\n"
                				+ " + desp: " + entry.getValue().desp + "\n"
                			//	+ "--------------------------------\n"
                		);
            }
            
             writer.write("\n");
            }
            
            writer.write("--------------------------------\n");
            int numT=2;
            for (Map.Entry<String, TreeMap<String, ID>> entry : TDS.TDSFunc.entrySet()) {
                writer.write("TABLA DE LA FUNCION " +  entry.getKey() + " #"+numT+"\n\n");
                numT++;
            //    String treeMapKey = entry.getKey();
                Map<String, ID> nestedMap = entry.getValue();
                
                for (Map.Entry<String, ID> nestedEntry : nestedMap.entrySet()) {
                //	String treeMapKey2 = nestedEntry.getKey();
                 //   HashMap<Integer, Atributo> nestedMap2 = nestedEntry.getValue().Atributos;                	
                	writer.write(                		
                    		"* '" + nestedEntry.getKey() + "'\n" /*+ " + lexema:" + nestedEntry.getValue().lexema + "\n"*/
                    				+ " + tipo: '" + nestedEntry.getValue().tipo + "'\n"
                    				+ " + desp: " + nestedEntry.getValue().desp + "\n\n");
            	/*for (Map.Entry<Integer, Atributo> nestedEntry2 : nestedMap2.entrySet()) {
            		writer.write(" + ATRIBUTO:\n" + "  + tipo:" + nestedEntry2.getValue().tipoAt + "\n"
            				+ "  + lexema:" + nestedEntry2.getValue().lexAt + "\n"
            				+ "  + desp:" + nestedEntry2.getValue().desp + "\n"
            				+ "  + tipoParametro:" + nestedEntry2.getValue().tipoParam + "\n"
            				);
            	}*/
            }
                writer.write("--------------------------------\n");
            
            }
            System.out.println("La TDS ha sido escrita en el fichero " + filename);
        
		
		
		
		} catch (IOException e) {
            System.err.println("Error escribiendo en el fichero: " + e.getMessage());
        }
	}

	public static void main(String[] args) throws IOException {
		TablaDeSimbolos TDS = new TablaDeSimbolos(new TreeMap<String, ID>());
		AnalizadorLexico analizador = new AnalizadorLexico("input.txt", TDS);
		AnalizadorSintactico aS = new AnalizadorSintactico(analizador, TDS);
		System.out.println(TDS.TDS.toString());
		System.out.println(TDS.TDSFunc.toString());
		BufferedWriter writerPars = new BufferedWriter(new FileWriter("parse.txt"));
		meteEnFichero(TDS,"tablaDeSimbolos.txt");
		writerPars.write(parse);
		writerPars.close();
		// writerTok.close();
		System.out.println(parse);
	}

}