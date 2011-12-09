/*
 * Copyright (C) 2006-2011 Thomas Chemineau
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */


package net.aepik.alasca.plugin.schemaconverter.core;

import net.aepik.alasca.util.Node;
import java.io.File;
import java.util.Enumeration;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Cette classe sert à faire la traduction entre des noms.
 * Ce traducteur se base sur un fichier XML où sont répertoriés tous les
 * mots et leurs traductions pour chaque syntaxe présente.
 * <br/><br/>
 * Un tel fichier doit respecter des règles biens précises pour être lues
 * correctement.
 * <br/><br/>
 * Techniquement, les données sont représentées sous forme d'arbres, tel que :
 * <br/><br/>
 * <pre>
 * Dictionnaires
 * |
 * Dictionnaire1
 * |
 * |_ SOURCE
 * |  |_ source1
 * |  |_ source2...
 * |
 * |_ DESTINATION
 * |  |_ destination1
 * |  |_ destination2...
 * |
 * |_ KEY
 *    |_ key1
 *    |  |_ VALUE
 *    |  |_ EQUIV
 *    |    |_ equiv1
 *    |    |_ equiv2...
 *    |      |_ valeur1
 *    |      |_ valeur2
 *    |_ key2...
 * </pre>
 * <br/><br/>
 */
public class Translator
{

	private static String DESTINATION = "destination";

	private static String KEY = "key";

	private static String KEY_EQUIV = "keyequiv";

	private static String KEY_EQUIV_VALUE = "value";

	private static String KEY_NAME = "keyname";

	private static String KEY_NAME_VALUE = "value";

	private static String SOURCE = "source";

	private static String SYNTAX = "syntax";

	private static String SYNTAX_VALUE_NAME = "name";

	private static String TRANSLATOR = "translator";

	/**
	 * Le nom du fichier XML
	 * @var String
	 */
	private String filename;

	/**
	 * Le dictionnaire
	 * @var Node
	 */
	private Node dictionnaries;

	/**
	 * Le dictionnaire en cours d'utilisation
	 * @var Node
	 */
	private Node currentDictionnary;

	/**
	 * Construit une nouvelle instance d'un traducteur.
	 * @param f Le nom du fichier contenant toutes les traductions.
	 */
	public Translator ( String f )
	{
		filename = f;
		dictionnaries = null;
		currentDictionnary = null;
	}

	/**
	 * Créer un traducteur depuis un fichier XML.
	 * @param filename Le nom du fichier XML.
	 * @return Translator Un nouveau translator.
	 */
	public static Translator create ( String filename )
	{
		Translator traduc = new Translator(filename);
		if (traduc.load())
		{
			return traduc;
		}
		return null;
	}

	/**
	 * Retourne l'ensemble des noms de dictionnaires disponibles
	 * pour ce traducteur.
	 * @return String[] L'ensemble des noms de dictionnaires.
	 */
	public String[] getAvailableDictionnaries ()
	{
		if (dictionnaries != null)
		{
			return dictionnaries.getNodesValue();
		}
		return null;
	}

	/**
	 * Retourne l'ensemble des noms de syntaxes source.
	 * @return String[] L'ensemble des noms de syntaxes source.
	 */
	public String[] getDestinationSyntaxes ()
	{		
		if (currentDictionnary == null)
		{
			return null;
		}
		Node n = currentDictionnary.getNode(DESTINATION);
		return n.getNodesValue();
	}

	/**
	 * Retourne les valeurs d'une clef.
	 * @param key L'identifiant de clef (dans le retour de getKeys par ex).
	 * @return String[] Un ensemble de valeurs pour une clef.
	 */
	public String[] getKeyValues ( String key )
	{
		Node n = currentDictionnary.getNode(key);
		if (n != null)
		{
			return n.getNodeValues(KEY_NAME_VALUE);
		}
		return null;
	}

	/**
	 * Retourne les clefs équivalentes pour une clef spécifique avec sa valeur
	 * spécifique. La valeur peut-être null, dans ces cas, c'est la première
	 * clef rencontrée qui sera utilisée.
	 * @param key La clef.
	 * @param keyValue La valeur de la clef, car une clef peut être définie par
	 *		son nom de clef ET sa valeur.
	 * @return String[] Un ensemble de clefs équivalentes.
	 */
	public String[] getKeyEquivs ( String key, String keyValue )
	{
		Node[] nodes = currentDictionnary.getNode(KEY).getNodes(key);
		if (nodes != null || nodes.length != 0)
		{
			Node n = null;
			for (int i = 0; i < nodes.length && n == null; i++)
			{
				if (keyValue == null)
				{
					n = nodes[i];
				}
				else
				{
					String[] tmp = nodes[i].getNodeValues(KEY_NAME_VALUE);
					for (int j = 0; tmp != null && n == null && j < tmp.length; j++)
					{
						if (tmp[j].equals(keyValue))
						{
							n = nodes[i];
						}
					}
				}
			}
			if (n != null)
			{
				return n.getNode(KEY_EQUIV).getNodesValue();
			}
		}
		return null;
	}

	/**
	 * Retourne les valeurs d'une clef équivalente spécifique pour une clef.
	 * @param key La clef.
	 * @param keyValue La valeur de la clef, car une clef peut être définie par
	 *		son nom de clef ET sa valeur.
	 * @param keyEquiv Une clef équivalente.
	 * @return String[] Les valeurs de cette clef équivalente.
	 */
	public String[] getKeyEquivValues ( String key, String keyValue, String keyEquiv )
	{
		Node[] nodes = currentDictionnary.getNode(KEY).getNodes(key);
		if (nodes != null || nodes.length != 0)
		{
			Node n = null;
			for (int i = 0; i < nodes.length && n == null; i++)
			{
				if (keyValue == null)
				{
					n = nodes[i];
				}
				else
				{
					String[] tmp = nodes[i].getNodeValues(KEY_NAME_VALUE);
					for (int j = 0; tmp != null && n == null && j < tmp.length; j++)
					{
						if (tmp[j].equals(keyValue))
						{
							n = nodes[i];
						}
					}
				}
			}
			if (n != null)
			{
				return n.getNode(KEY_EQUIV).getNodeValues(keyEquiv);
			}
		}
		return null;
	}

	/**
	 * Retourne l'ensemble des clefs contenues dans le dictionnaire courant.
	 * @return String[] Un ensemble de chaîne de caractères.
	 */
	public String[] getKeys ()
	{
		return currentDictionnary.getNodeValues(KEY);
	}

	/**
	 * Retourne l'ensemble des noms de syntaxes source.
	 * @return String[] L'ensemble des noms de syntaxes source.
	 */
	public String[] getSourceSyntaxes ()
	{	
		if (currentDictionnary == null)
		{
			return null;
		}
		Node n = currentDictionnary.getNode(SOURCE);
		return n.getNodesValue();
	}

	/**
	 * Teste si une entrée pour une clef donnée existe dans le dictionnaire courant.
	 * @param key Une clef.
	 * @return boolean True si l'entrée existe, false sinon.
	 */
	public boolean isKeyExists ( String key )
	{
		return currentDictionnary.getNode(KEY).contains(key);
	}

	/**
	 * Teste si une entrée pour une clef donnée existe dans le dictionnaire courant.
	 * On teste en fonction du nom de clef et de sa valeur.
	 * @param key Une clef.
	 * @param value Une valeur.
	 * @return boolean True si l'entrée existe, false sinon.
	 */
	public boolean isKeyExists ( String key, String value )
	{
		Node n = currentDictionnary.getNode(KEY);
		Node[] tmp = n.getNodes(key);
		boolean ok = false;
		for (int i = 0; tmp != null && i < tmp.length && !ok; i++)
		{
			String[] values = n.getNodeValues(KEY_NAME_VALUE);
			for (int j = 0; values != null && j < values.length && !ok; j++)
			{
				if (values[j].equals(value))
				{
					ok = true;
				}
			}
		}
		return ok;
	}

	/**
	 * Charge les données XML en mémoire.
	 * @return boolean True si l'opération est un succès, false sinon.
	 */
	public boolean load ()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File(filename), new TranslatorParser());
			return true;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return false;
	}

	/**
	 * Sélectionne le dictionnaire en cours d'utilisation.
	 * @param dictionnaryName Le nom du dictionnaire à sélectionner.
	 * @return boolean True si la sélection réussie, false sinon.
	 */
	public boolean setSelectedDictionnary ( String dictionnaryName )
	{
		if (dictionnaries.contains(dictionnaryName))
		{
			currentDictionnary = dictionnaries.getNode(dictionnaryName);
			return true;
		}
		return false;
	}

	/**
	 * La classe privée suivante va nous permettrent de lire
	 * correctement le fichier XML, et de créer notre traducteur.
	 */
	private class TranslatorParser extends DefaultHandler
	{

		private Node tempDictionnaries = null;

		private Node tempCurrentDictionnary = null;

		private Node tempCurrentKey = null;

		private Node tempCurrentKeyEquiv = null;

		private Node tempCurrentKeys = null;

		private Node tempCurrentKeysEquiv = null;

		private String buffer = null;

		public void startElement ( String namespaceURI, String simpleName, String qualifiedName, Attributes attrs ) throws SAXException
		{
			String nomElement = simpleName.equals("") ? qualifiedName : simpleName;
			if (nomElement.equals(TRANSLATOR))
			{
				tempDictionnaries = new Node();
			}
			else if (nomElement.equals(SYNTAX))
			{
				tempCurrentDictionnary = new Node(attrs.getValue(SYNTAX_VALUE_NAME));
				for (String type : new String[] {DESTINATION,SOURCE})
				{
					Node n = new Node(type);
					for (String s : attrs.getValue(type).split(","))
					{
						n.add(new Node(s));
					}
					tempCurrentDictionnary.add(n);
				}
				tempCurrentDictionnary.add(new Node(KEY));
			}
			else if (nomElement.equals(KEY))
			{
				tempCurrentKeys = new Node();
				tempCurrentKeysEquiv = new Node(KEY_EQUIV);
			}
			else if (nomElement.equals(KEY_NAME))
			{
				tempCurrentKey = new Node();
				Node n1 = new Node(KEY_NAME_VALUE, new Node[]{new Node(attrs.getValue(KEY_NAME_VALUE))});
				tempCurrentKey.add(n1);
				buffer = null;
			}
			else if (nomElement.equals(KEY_EQUIV))
			{
				tempCurrentKeyEquiv = new Node();
				Node n1 = new Node(attrs.getValue(KEY_EQUIV_VALUE));
				tempCurrentKeyEquiv.add(n1);
				buffer = null;
			}
		}

		public void endElement ( String namespaceURI, String simpleName, String qualifiedName ) throws SAXException
		{
			String nomElement = simpleName.equals("") ? qualifiedName : simpleName;
			if (nomElement.equals(TRANSLATOR))
			{
				Translator.this.dictionnaries = tempDictionnaries;
				tempDictionnaries = null;
			}
			else if (nomElement.equals(SYNTAX))
			{
				if (tempDictionnaries.contains(tempCurrentDictionnary.getValue()))
				{
					throw new SAXException("Erreur de syntaxe XML: 2 syntaxes de noms identiques");
				}
				else
				{
					tempDictionnaries.add(tempCurrentDictionnary);
					tempCurrentDictionnary = null;
				}
			}
			else if (nomElement.equals(KEY))
			{
				if (tempCurrentDictionnary == null)
				{
					throw new SAXException("Erreur de syntaxe XML");
				}
				else
				{
					Node keys = tempCurrentDictionnary.getNode(KEY);
					Node[] keysNode = tempCurrentKeys.getNodes();
					for (int i = 0; keysNode != null && i < keysNode.length; i++)
					{
						boolean ajout = true;
						String[] val1 = keysNode[i].getNodeValues(KEY_NAME_VALUE);
						Node tmp = keys.getNode(keysNode[i].getValue());
						if (tmp != null)
						{
							String[] val2 = tmp.getNodeValues(KEY_NAME_VALUE);
							if (val2.length == 1 && val1.length == 1 && val1[0].equals(val2[0]))
							{
								ajout = false;
							}
						}
						if (ajout)
						{
							keysNode[i].add(tempCurrentKeysEquiv);
							keys.add(keysNode[i]);
						}
					}
					tempCurrentKeys = null;
					tempCurrentKeysEquiv = null;
				}
			}
			else if (nomElement.equals(KEY_NAME))
			{
				tempCurrentKey.setValue(buffer);
				tempCurrentKeys.add(tempCurrentKey);
				tempCurrentKey = null;
				buffer = null;
			}
			else if (nomElement.equals(KEY_EQUIV))
			{
				tempCurrentKeyEquiv.setValue(buffer);
				tempCurrentKeysEquiv.add(tempCurrentKeyEquiv);
				tempCurrentKeyEquiv = null;
				buffer = null;
			}
		}
	
		public void characters ( char buf[], int offset, int len ) throws SAXException
		{
			buffer = new String(buf, offset, len);
		}
	
		public void error ( SAXParseException e ) throws SAXParseException
		{
			throw e;
		}

		public void startDocument () throws SAXException
		{
		}

		public void endDocument () throws SAXException
		{
		}

	}

}

