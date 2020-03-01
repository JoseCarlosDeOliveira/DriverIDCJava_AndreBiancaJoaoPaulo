package Utilitaries;

public class Utilitaries {

	// aqui ele esta declarando uma constante global ou seja o valor dela nao muda mas
		private static final String hexDigits = "0123456789ABCDEF";
		//private static byte[] byaConvToBin = null;
		/**
		 * Converts the byte array into a hexadecimal representation.
		 * @param input - The array of bytes to be converted.
		 * @return A string with the hex representation of the array
		 */
		 // byteArray para HexString
		public static String byteArrayToHexString(byte[] b) {
			//StringBuffer  conjunto de strings  ela já é do java
			StringBuffer buf = new StringBuffer();
			
			
		    // b.length byte que foi passado como parametro na chamada da funcao
			for (int i = 0; i < b.length; i++) {
				int j = ((int) b[i]) & 0xFF;
				buf.append(hexDigits.charAt(j / 16));//UpperHexDigit
				buf.append(hexDigits.charAt(j % 16));//LowerHexDigit
			}
			return buf.toString();
		}

		/**
		 * Converts a hex string in the corresponding array of bytes.
		 * @param Hex - The Hex String
		 * @return The array of bytes
		 * @throws IllegalArgumentException - If the String does not sej Sitio
		 * valid representation haxadecimal
		 */
		public static byte[] hexStringToByteArray(String hexString)
				throws IllegalArgumentException {
		
			// check if the string pair has a number of elements
			if (hexString.length() % 2 != 0) {
				throw new IllegalArgumentException("Fail Invalid HexString[" + hexString + "]");
			}
	        //Create a byte array size of HexString.length/2	
			byte[] b = new byte[hexString.length() / 2];
			for (int i = 0; i < hexString.length(); i += 2) {
				if(((hexString.charAt(i) >= '0') && (hexString.charAt(i) <= '9')) ||
			    	((hexString.charAt(i) >= 'a') && (hexString.charAt(i) <= 'f'))||  
			    		((hexString.charAt(i) >= 'A') && (hexString.charAt(i) <= 'F'))) {
				    //Store HexByte into a byte array position 
					b[i / 2] = (byte) ((hexDigits.indexOf(Character.toUpperCase(hexString.charAt(i))) << 4) | (hexDigits
							.indexOf(Character.toUpperCase(hexString.charAt(i + 1)))));
				} else {
				    //Fail There are no HexString('0'to 'F') at current position 
					throw new IllegalArgumentException("Fail Invalid HexString[" + hexString + "] Wrong Digit[" + hexString.charAt(i) + "] at["+i+"]");
				}
			}
			return b;
		}	
		
		// convertentendo de ascii sankyo para binario
		public static byte[] CovASCIIhexSankyoBin(byte[] pusASCI) {
			int i;
			int o = 0;
			byte[] byaConvToBin = new byte[pusASCI.length/2];
			
			for(i=0; i < pusASCI.length-1; i++){
				if (o < byaConvToBin.length -1){
					byaConvToBin[o] = (byte) ((pusASCI[i++] << 4)&0xF0); //High
					byaConvToBin[o++] |= (pusASCI[i]&0x0F); //Low
				}
			}
			
			System.out.println("ASCII em BIN [" + byteArrayToHexString(byaConvToBin) +"]");
			return byaConvToBin;
			
			
		}
		// converte bin ascii hex para sankyo
		public static byte[] CovBinASCIIhexSankyo(byte[] pusBinArray) {
			int i;
			int o = 0;
			byte[] byaConvToHex = new byte[pusBinArray.length*2];
			
			for(i=0; i < pusBinArray.length ; i++){
	
					byaConvToHex[o++] = (byte) (((pusBinArray[i] >> 4)&0x0F) | 0x30); //High
					byaConvToHex[o++] = (byte) ((pusBinArray[i]&0x0F) | 0x30); //Low
				
			}
			
			System.out.println("BIN em ASCII [" + byteArrayToHexString(byaConvToHex) +"]");
			return byaConvToHex;
			
		}


}
