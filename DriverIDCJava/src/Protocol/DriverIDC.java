package Protocol;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor.INPUT_STREAM;
import javax.swing.JOptionPane;
import javax.swing.plaf.SliderUI;

import Utilitaries.Utilitaries;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class DriverIDC {
	
	static int lineStatus[];
	static String porta = "COM1";
	static SerialPort sp = new SerialPort(porta);

	static private int iMediaStatus = 0;
	static private int iTrackStatus = 0;
	static private int iError = 0;

	// variavel que controla se recebeu o STX
	static boolean bSTXReceived = false;
	static boolean bETXReceived = false;
	static boolean bCmdEmProcesso = false;

	static byte[] byCmd = null; //Cm + pm
	static byte[] byCmdParams = null; //Parameters
	static byte[] bytesRead = null;
	// comando do byte
	private static byte[] byarrayCmdPacket;
	
	
	private static byte[] byarrayRspPacket;
	private static byte[] byarrayRsp = null;
	
	
	public static char[] r1 = new char[18];
	public static char[] r2 = new char[28];
	
	
	public static byte[] byaConvToBin = null;
	public static byte[] byaConvToBin1 = null;
	public static int iTrackAvailable;
	
	
	static List<byte[]> bufer = new ArrayList<byte[]>();
	
	
	public static String sRAPDU = "";
	

	// função que irá ser chamada no execultavel esses metodos são
	// ==================PUBLIC==================
	// 1
	public static int abrirComunicacao() {
		int rc = -100;
		try {
			rc = abrirPorta();
			if (rc < 0) {
				System.err.println("falha ao abrir a porta [" + rc + "]");

			}
			Thread.sleep(750);
			rc = InitializeICRW(2,2,2);
			if (rc < 0) {
				System.err.println("falhou initializeICRW [" + rc + "]");
				fecharPorta();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rc;
	}

	// 2
	public static int FecharComunicacao() {
		int rc = -110;
		try {
			rc = fecharPorta();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rc;
	}

	// 3
	public static int SupervisorInitialize() throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -111;
		byCmd = new byte[1];
		byCmd[0] = (byte) 0x30;// CMD rc

		byCmdParams = new byte[1];
		byCmdParams[0] = (byte) 0x30;// PM rc
		rc = processaComando(1, byCmd, byCmdParams, 1000);
		if (rc < 0) {
			System.out.println("SupervisorInitialize falhou" + rc);
			return rc;
		}
		return rc;
	}

	// 4
	public static int GetSupervisorRevision(char SupervisorRevision) throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -112;
		int iRspLen = 0;
		
		byCmd = new byte[1];
		byCmd[0] = (byte) 0x41;// CMD rc

		byCmdParams = new byte[1];
		byCmdParams[0] = (byte) 0x30;// PM rc
		rc = processaComando(1, byCmd, byCmdParams,1000);
		//rc = processaComando( bytesWrite, bytesWrite2,SupervisorRevision,&iRspLen);
		
		
		if (rc < 0) {

			System.out.println("GetSupervisorRevision falhou" + rc);
		} else {
			for(int x = 0; x <= byarrayRsp.length - 2; x++) {
				System.out.println("RESPOSTA " + (char)byarrayRsp[x]);
				r1[x] =  (char)byarrayRsp[x];
			}
		}
		return rc;
	}

	// 5
	public static int AreaSwitch() throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -113;
		byCmd = new byte[1];
		byCmd[0] = (byte) 0x4B;// CMD rc

		byCmdParams = new byte[1];
		byCmdParams[0] = (byte) 0x30;// PM rc
		rc = processaComando(1, byCmd, byCmdParams, 1000);
		if (rc < 0) {
			System.out.println("AreaSwitch falhou" + rc);
		
		}
		return rc;
		
	}

	// 6
	public static int InitializeICRW(int iOpcaoLed, int iLatch, int iLedFlash) throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -114;
		byCmd = new byte[2]; //cm + pm
		byCmdParams = new byte[2]; //cm + pm
		byCmd[0] = (byte) 0x30;// cm
		switch(iOpcaoLed){
		case 0:
			byCmd[1] = (byte) 0x30;// pm
			break;
		case 1:
			byCmd[1] = (byte) 0x31;// pm
			break;
		case 2:
			byCmd[1] = (byte) 0x32;// pm
			break;
		case 3:
			byCmd[1] = (byte) 0x33;// pm
			break;
		case 4:
			byCmd[1] = (byte) 0x34;// pm
			break;
		case 5:
			byCmd[1] = (byte) 0x35;// pm
			break;
		case 6:
			byCmd[1] = (byte) 0x36;// pm
			break;
		case 7:
			byCmd[1] = (byte) 0x37;// pm
			break;
		default:
			return rc;
		}
		switch(iLatch){
		case 0:
			byCmdParams[0] = (byte) 0x30;// lt
			break;
		case 1:
			byCmdParams[0] = (byte) 0x31;// lt
			break;
		case 2:
			byCmdParams[0] = (byte) 0x32;// lt
			break;
		case 4:
			byCmdParams[0] = (byte) 0x34;// lt
			break;
		case 8:
			byCmdParams[0] = (byte) 0x38;// lt
			break;
		default:
			rc = -915;
			return rc;
		}
		switch(iLedFlash){
		case 0:
			byCmdParams[1] = (byte) 0x30;// ft
			break;
		case 1:
			byCmdParams[1] = (byte) 0x31;// Ft
			break;
		case 2:
			byCmdParams[1] = (byte) 0x32;// ft
			break;
		case 3:
			byCmdParams[1] = (byte) 0x33;// ft
			break;
		case 4:
			byCmdParams[1] = (byte) 0x34;// ft
			break;
		case 5:
			byCmdParams[1] = (byte) 0x35;// ft
			break;
		default:
			rc = -915;
			return rc;
		}
		
		
		rc = processaComando(1, byCmd, byCmdParams, 1000);
        return rc;
	}

	// 7
	public static int GetStatus() throws IllegalArgumentException,
			InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -115;

		byCmd = new byte[1];
		byCmd[0] = (byte) 0x31;// CMD rc

		byCmdParams = new byte[1];
		byCmdParams[0] = (byte) 0x30;// PM rc

		rc = processaComando(1, byCmd, byCmdParams, 1000);
		if (rc < 0) {
			System.out.println("falhou Get status" + rc);
		}
		return rc;
	}

	// 8
	public static int MagTrackRead(int iOpcaoTrack) throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -116;
		byCmd = new byte[2];
		
		switch (iOpcaoTrack) {
		case 1:
			byCmd[0] = (byte) 0x32;// CMD rc
			byCmd[1] = (byte) 0x31;// PM rc
			
		
			break;
		case  2:
			
			byCmd[0] = (byte) 0x32;// CMD rc
			byCmd[1] = (byte) 0x32;// PM rc
		
			
			break;
			
		case 3:
			
			byCmd[0] = (byte) 0x32;// CMD rc
			byCmd[1] = (byte) 0x33;// PM rc
			
		
			break;

		default:
			
			break;
		}
		
		
		
		
		rc = processaComando(1, byCmd, null, 1000);
		
		if (rc < 0) {
			System.out.println("MagTrackRead falhou" + rc);
		}
		return rc;
	}

	// 9
	public static int EraseTracks() throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -117;
		byCmd = new byte[1];
		byCmd[0] = (byte) 0x32;// CMD rc

		byCmdParams = new byte[1];
		byCmdParams[0] = (byte) 0x36;// PM rc
		rc = processaComando(1, byCmd, byCmdParams, 1000);
		if (rc < 0) {
			System.out.println("EraseTracks falhou" + rc);
		}
		return rc;
	}

	// 10
	public static int SetLed(int iOpcaoSetLed,int iOpcaoSetFlash) throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -118;
		byCmd = new byte[2]; //cm + pm
		byCmdParams = new byte[1]; //cm + pm
		byCmd[0] = (byte) 0x33;// cm
		switch(iOpcaoSetLed){
		case 0:
			byCmd[1] = (byte) 0x30;// pm
			break;
		case 1:
			byCmd[1] = (byte) 0x31;// pm
			break;
		case 2:
			byCmd[1] = (byte) 0x32;// pm
			break;
		case 3:
			byCmd[1] = (byte) 0x33;// pm
			break;

		default:
			return rc;
		}
		
	
		switch(iOpcaoSetFlash){
		case 0:
			byCmdParams[0] = (byte) 0x30;// ft
			break;
		case 1:
			byCmdParams[0] = (byte) 0x31;// Ft
			break;
		case 2:
			byCmdParams[0] = (byte) 0x32;// ft
			break;
		case 3:
			byCmdParams[0] = (byte) 0x33;// ft
			break;
		case 4:
			byCmdParams[0] = (byte) 0x34;// ft
			break;
		case 5:
			byCmdParams[0] = (byte) 0x35;// ft
			break;
		default:
			rc = -1001;
			return rc;
		}
		
		
		rc = processaComando(1, byCmd, byCmdParams, 1000);
        return rc;

	}

	// 11
	public static int GetInfoEMV() throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -119;
		byCmd = new byte[1];
		byCmd[0] = (byte) 0x36;// CMD rc

		byCmdParams = new byte[1];
		byCmdParams[0] = (byte) 0x30;// PM rc
		rc = processaComando(1, byCmd, byCmdParams, 1000);
		if (rc < 0) {
			System.out.println("GetInfoEMV falhou" + rc);
		}
		return rc;
	}

	// 12
	public static int GetInfoGIE() throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -120;
		byCmd = new byte[1];
		byCmd[0] = (byte) 0x36;// CMD rc

		byCmdParams = new byte[1];
		byCmdParams[0] = (byte) 0x31;// PM rc
		rc = processaComando(1, byCmd, byCmdParams, 1000);
		if (rc < 0) {
			System.out.println("GetInfoGIE em obras" + rc);
		}else {
			for(int x = 0; x <= byarrayRsp.length - 1; x++) {
				System.out.println("RESPOSTA " + (char)byarrayRsp[x]);
				r2[x] =  (char)byarrayRsp[x];
			}
		}
		return rc;

	}

	// 13
	public static int GetPassCounter() throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -121;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x36;// CMD rc
		byCmd[1] = (byte) 0x32;

		rc = processaComando(1, byCmd, null, 1000);
		if (rc < 0) {
			System.out.println("GetPassCounter Falhou" + rc);
		}else{
			System.out.println("Deu certo!");
		}
		return rc;
	}

	// 14 //destravar cartao
	public static int OpenShutter() throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -122;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x40;// CMD rc
		byCmd[1] = (byte) 0x30;

		rc = processaComando(1, byCmd, null, 1000);
		if (rc < 0) {
			System.out.println("OpenShutter Falhou" + rc);
		}else{
			System.out.println("Deu certo!");
		}
		return rc;
	}

	// 15
	public static int GetUserRevision() throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -123;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x41;// CMD rc
		byCmd[1] = (byte) 0x31;

		rc = processaComando(1, byCmd, null, 1000);

		if (rc < 0) {
			System.out.println("GetUserRevision falhou" + rc);
		}else{
			System.out.println("Deu certo!");
		}
		return rc;
	}

	// 16
	public static int GetEMVRevision() throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -124;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x41;// CMD rc
		byCmd[1] = (byte) 0x32;

		rc = processaComando(1, byCmd, null, 1000);

		if (rc < 0) {
			System.out.println("GetEMVRevision falhou" + rc);
		}else{
			System.out.println("Deu certo!");
		}
		return rc;
	}

	// 17
	public static int ActivateICC() throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -125;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x49;// CMD rc
		byCmd[1] = (byte) 0x30;

		rc = processaComando(1, byCmd, null, 1000);
		if (rc < 0) {
			System.out.println("ActivateICC falhou" + rc);
		}else{
			byaConvToBin = Utilitaries.CovASCIIhexSankyoBin(byarrayRsp);
			//byaConvToBin = Utilitaries.CovBinASCIIhexSankyo(byaConvToBin);
		}
		return rc;

	}

	// 18
	public static int DeactivateICC() throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -126;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x49;// CMD rc
		byCmd[1] = (byte) 0x31;

		rc = processaComando(1, byCmd, null, 1000);
		if (rc < 0) {
			System.out.println("DeactivateICC falhou" + rc);
		}
		return rc;

	}

	// 19
	public static int StatusICC() throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -127;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x49;// CMD rc
		byCmd[1] = (byte) 0x32;

		rc = processaComando(1, byCmd, null, 1000);
		if (rc < 0) {
			System.out.println("StatusICC em obras" + rc);
		}
		return rc;

	}

	// 20
	public static int ICCcommT0(byte[] byaCAPDU) throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -128;
		
		byCmd[0] = (byte) 'I';// CMD rc
		byCmd[1] = (byte) '3';
		
		byCmdParams = Utilitaries.CovBinASCIIhexSankyo(byaCAPDU);
		
		
		//System.arraycopy(Utilitaries.CovBinASCIIhexSankyo(byaCAPDU), 0, byCmdParams, 0, byCmdParams.length);
		
		
		rc = processaComando(1, byCmd, byCmdParams, 1000);
		
		if (rc < 0) {
			System.out.println("ICCcommT0 falhou!" + rc);
		}else{
			sRAPDU = "";
			for(int j = 0; j < byarrayRsp.length; j++){
				sRAPDU += (char) byarrayRsp[j];
			}
			System.out.println("Deu certo !");
			
		}
		return rc;
	}

	// 21
	public static int WammResetICC() throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -129;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x49;// CMD rc
		byCmd[1] = (byte) 0x38;

		rc = processaComando(1, byCmd, null, 1000);
		if (rc < 0) {
			System.out.println("WammResetICC falhou" + rc);
		}else{
			byaConvToBin1 = Utilitaries.CovASCIIhexSankyoBin(byarrayRsp);
			//byaConvToBin = Utilitaries.CovBinASCIIhexSankyo(byaConvToBin);
			rc = 19;
		}
		return rc;
	}

	// 22
	public static int TimerSet() throws InterruptedException, SerialPortException, SerialPortTimeoutException {

		int rc = -130;
		byCmd = new byte[2];
		byCmd[0] = (byte) 0x57;// CMD rc
		byCmd[1] = (byte) 0x30; //30 31
		
		byCmdParams = new byte[2];
		byCmdParams[0] = 15;
		byCmdParams[1] = 95;
		
		

		rc = processaComando(1, byCmd, null, 1000);
		if (rc < 0) {
			System.out.println("WammResetICC falhou" + rc);
		}else{
			//byaConvToBin1 = Utilitaries.CovASCIIhexSankyoBin(byarrayRsp);
			//byaConvToBin = Utilitaries.CovBinASCIIhexSankyo(byaConvToBin);
			//rc = 19;
			System.out.println("deu certo");
		}
		return rc;
	}

	// esse metodos aqui são todos locais ==========PRIVATE====================
	// / funções que não serão chamadas diretamente no excultavel ///
	private static int abrirPorta() {
		int rc = -101;

		try {
			sp.openPort();

		} catch (SerialPortException e) {
			e.printStackTrace();
			return rc;
		}
		try {
			sp.setDTR(true);
			sp.setRTS(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			lineStatus = sp.getLinesStatus();

		} catch (Exception e) {
			e.printStackTrace();

			try {
				fecharPorta();
			} catch (Exception e2) {
				e.printStackTrace();
			}
		}
		System.out.println("foi 1?");
		try {
			System.out.println("foi 2?");
			// configurando porta
			sp.setParams(SerialPort.BAUDRATE_38400, SerialPort.DATABITS_7,// pode
																			// ser
																			// DATABITS_8
																			// basta
																			// vim
																			// aqui
																			// e
																			// mudar
					SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
			System.out.println("foi 4?");
			rc = 1;
		} catch (Exception e) {
			System.err.println("ERRO na configuração de porta "
					+ e.getMessage());
			System.out.println("foi 3?");
			return rc;

		}
		/*
		 * try { bytesWrite = new byte[1]; bytesWrite[0] = (byte) 0x31;// CMD rc
		 * = processaComando(1, bytesWrite, null, 1000);
		 * 
		 * System.out.println("RC " + rc); } catch (Exception e) {
		 * e.printStackTrace(); fecharPorta(); }
		 */
		return rc;
	}

	private static int fecharPorta() {
		int rc = -102;
		try {
			sp.closePort();
			rc = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rc;
	}

	private static int montarPacoteCmd(int iProtocol, byte[] byaCmd,
			byte[] byaCmdParam, int iTimeout) {
		int rc = -103;
		int i;
		// variavel que irá receber o calculo do LRC
		byte byteLRC = 0;

		try {
			if (iProtocol == 1) {
				// verificando se o comando tem parametro
				if (byaCmdParam != null) {
					// MONTA O PACOTE COM O TAMANHO DO COMANDO + MAIS O DO
					// PARAMETRO + 4 QUE É O ESPAÇO
					// PARA O STX, LEN, ETX E LRC
					byarrayCmdPacket = new byte[byaCmd.length
							+ byaCmdParam.length + 4];

					byarrayCmdPacket[0] = 0x02;// INSERE NA PRIMEIRA POSIÇÃO O
					byarrayCmdPacket[1] = 'C';// INSERE NA PRIMEIRA POSIÇÃO O
					// byarrayCmdPacket[1] = (byte) (byaCmd.length +
					// byaCmdParam.length); // LEN
				} else {
					// montando o pacote sem o parametro
					byarrayCmdPacket = new byte[byaCmd.length + 4];

					byarrayCmdPacket[0] = 0x02;// /STX
					byarrayCmdPacket[1] = 'C';// INSERE NA PRIMEIRA POSIÇÃO O
					// byarrayCmdPacket[1] = (byte) (byaCmd.length);// /LEN
				}
				// colocando o comando no pacote, duas posições depois
				for (i = 0; i < byaCmd.length; i++) {
					byarrayCmdPacket[i + 2] = byaCmd[i];// cm+pm
				}
				System.out.println(" Byte comando ["
						+ Utilitaries.byteArrayToHexString(byaCmd) + "]");
				if (byaCmdParam != null) {
					// aqui ta retornando o parametor certo
					System.out.println(" Byte comando parametro ["
							+ Utilitaries.byteArrayToHexString(byaCmdParam)
							+ "]");

					System.out.println(" tamanho do pacote de comando ["
							+ byarrayCmdPacket.length + "]");

					System.out.println(" tamanho do comando [" + byaCmd.length
							+ "]");

					/*
					 * for (i = 0; i < byarrayCmdPacket.length; i++) {
					 * //byarrayCmdPacket[i + byaCmd.length + 2] =
					 * byaCmdParam[1];// param byte[] aux = new byte[1]; aux[0]
					 * = byarrayCmdPacket[i];
					 * System.out.println("valor de byArrayCmdPakge posição [" +
					 * i + "] = " + Utilitaries.byteArrayToHexString(aux)); }
					 */

					for (i = 0; i < byaCmdParam.length; i++) {
						byarrayCmdPacket[i + byaCmd.length + 2] = byaCmdParam[i];// param
					}

				}
				byarrayCmdPacket[byarrayCmdPacket.length - 2] = 0x03; // ETX

				// calculo LRC
				for (i = 1; i < byarrayCmdPacket.length - 1; i++) {
					byteLRC ^= byarrayCmdPacket[i];
				}

				byarrayCmdPacket[byarrayCmdPacket.length - 1] = byteLRC; // LRC

				rc = byarrayCmdPacket.length;
				System.out.println("Protocolo enviado [" + iProtocol
						+ "] \n tamanho do pacote [" + rc
						+ "] \n pacote de de comando ["
						+ Utilitaries.byteArrayToHexString(byarrayCmdPacket)
						+ "] ok");
			}
			// / começando sem o LEN
			else if (iProtocol == 0) {
				// STX <Cmd><Param> ETX LRC(cmd > ETX)
				byarrayCmdPacket = new byte[byaCmd.length + byaCmdParam.length
						+ 3];

				byarrayCmdPacket[0] = 0x02;// INSERE NA PRIMEIRA POSIÇÃO O STX
				// colocando o comando no pacote, duas posições depois
				for (i = 0; i < byaCmd.length; i++) {
					byarrayCmdPacket[i + 1] = byaCmd[i];// cmd
				}

				for (i = 0; i < byarrayCmdPacket.length; i++) {
					byarrayCmdPacket[i + byaCmd.length + 1] = byaCmdParam[i];// param
				}

				// calculo LRC
				for (i = 1; i < byarrayCmdPacket.length - 2; i++) {
					byteLRC ^= byarrayCmdPacket[i];
				}

				byarrayCmdPacket[byarrayCmdPacket.length - 1] = byteLRC; // LRC
				rc = byarrayCmdPacket.length;
				System.out.println("Protocolo enviado [" + iProtocol
						+ "] \n tamanho do comando [" + rc
						+ "] \n pacote de de comando ["
						+ Utilitaries.byteArrayToHexString(byarrayCmdPacket)
						+ "] ok");
			} else {
				System.err.println("packCmd: Protocol[" + iProtocol + "] rc["
						+ rc + "] se o protocolo não for 0 nem 1");
				return rc;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rc;

	}

	private static int enviarPacoteCmd(byte[] byarrayCmdPkt, int iTimeout) {
		int rc = -104;
		boolean bEscritos = false;

		try {
			bEscritos = sp.writeBytes(byarrayCmdPkt);

			if (bEscritos == true) {
				rc = byarrayCmdPkt.length;
				System.out.println("Bytes escritos com sucesso! [" + rc + "]");
			} else {
				System.err.println(" rc = [" + rc + "] ERRO ");
			}
		} catch (SerialPortException e) {
			System.err.println("ERRO " + e.getMessage());
		}
		return rc;

	}

	private static int aguardarSTX(int iTimeout) throws InterruptedException {
		int rc = -105;
		int iAtraso = 0;
		byte[] byteRead = new byte[3];

		while (true) {

			// verificar se existem bytes
			try {
				System.out.println("SP getInput ["
						+ sp.getInputBufferBytesCount() + "]");
				System.out.println("SP getOutt ["
						+ sp.getOutputBufferBytesCount() + "]");
				if (sp.getInputBufferBytesCount() == 0) {
					if (++iAtraso == 10) {
						System.out.println("SP getInput2 ["
								+ sp.getInputBufferBytesCount() + "]");
						// caso não teja dados
						rc = -200;
						System.out.println("não recebeu dados [" + rc + "]");
						break;
					}
					Thread.sleep(100);
				}
			} catch (SerialPortException e) {
				e.printStackTrace();
				break;

			}

			// se existir algum byte na memoria de entrada
			try {
				// pega o byte da memoria e passa para a variavel
				System.out.println("Timeout [" + iTimeout + "]");
				// byteRead = sp.readBytes(1, iTimeout);

				byteRead = sp.readBytes(1, iTimeout);

				// System.out.println("Pegou os dados Teste" );
			} /*
			 * catch (SerialPortTimeoutException e) { e.printStackTrace();
			 * break;
			 * 
			 * }
			 */catch (Exception e) {
				e.printStackTrace();
				System.err.println("ERRO não pegou os dados");
				break;
			}

			// if(byteRead ==
			// null){System.out.println("linha 545 Ele e null");}else{System.out.println("linha 545 Ele não e null");}

			/*
			 * for(int x = 0; x <= byteRead.length; x++) { byte[] aux = new
			 * byte[1]; aux[0] = byteRead[x];
			 * System.out.println("Valor do ByteRead posicao [" + x + "] = [" +
			 * aux + "]"); }
			 */
			if (byteRead[0] == 0x06) { // ACK
				rc = 0;
				System.out.println("retornou ACK + [" + rc + "]");
				break;
			} else if (byteRead[0] == 0x02) { // STX
				rc = 1;
				System.out.println("retornou STX + [" + rc + "]");
				break;
			} else if (byteRead[0] == 0x15) { // NAK
				rc = -210;
				System.out.println("retornou NAK + [" + rc + "]");
				break;
			} else if (byteRead[0] == 0x00) { // NULL
				rc = -211;
				System.out.println("retornou NULL + [" + rc + "]");
				break;
			} else if (byteRead[0] < 0) { // FALHA
				rc = byteRead[0];
				System.out.println("FALHA RC = [" + rc
						+ "] \n esperava STX ACK ou NAK e Recebeu ["
						+ byteRead[0] + "]");
				break;
			} else {
				System.out.println("Esperar confimação: rc[" + rc
						+ "] ??? Recebeu[" + byteRead[0]
						+ "] Aguardava STX,ACK ou NAK Continue");
			}

		}
		return rc;

	}

	// equivalente ao ler pacote de respota
	private static int receberRespostaPKT(int iProtocol, int iTimeout)
			throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -106;
		int i = 0;
		byte byteLRCRec = 0;
		byte byteLRCCalc = 0;

		byte[] byteRead = new byte[1];
		byarrayRspPacket = new byte[1];

		if (bSTXReceived == true) {
			byarrayRspPacket[i++] = 0x02; // salva o stx no inicio do pacote de
											// resposta
		}

		// System.out.println("Pacote de resposta no receber resposta " +
		// Utilitaries.byteArrayToHexString(byarrayRspPacket));
		bETXReceived = false;
		int iAtraso = 0;

		/*
		 * try { while(sp.readBytes(1, iTimeout) != null) {
		 * 
		 * } } catch (SerialPortException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); } catch (SerialPortTimeoutException e1) {
		 * // TODO Auto-generated catch block e1.printStackTrace(); }
		 */

		
		while (true) {
			// Verificar se há pelo menos um byte de memória intermédia de
			// entrada
			try {
				if (sp.getInputBufferBytesCount() == 0) {
					iAtraso++;
					if (iAtraso >= 10) {
						rc = -998;
						System.err.println("nao retornou nem um byte na leitura["+ rc + "] tempo [" + iAtraso + "]");
						return rc;
					}
					Thread.sleep(100);
					continue;
				}
			} catch (SerialPortException e) {
				// TODO: handle exception
				rc = -213;
				System.err.println("valor do rc[" + rc + "] error "
						+ e.getMessage());
				break;
			}

			// lendo um byte do dispositivo
			try {

				byteRead = sp.readBytes(1, iTimeout);
				

			} catch (SerialPortException e) {
				// TODO: handle exception
				rc = -214;
				System.err.println("recvRspPkt: rc[" + rc + "] Fail e["
						+ e.getMessage() + "]");
				break;

			} catch (SerialPortTimeoutException e) {
				// TODO: handle exception
				System.err.println("receber pkt: rc[" + rc + "] Fail e["
						+ e.getMessage() + "] Timeout");
				break;
			}
			


			// / procuraremos explicações depois
			System.out.println("receberRespostaPKT Data["
					+ Utilitaries.byteArrayToHexString(byteRead)
					+ "]bETXReceived[" + bETXReceived + "]");

			if ((bSTXReceived == false) && (byteRead[0] == 0x02)) {

				System.out.println("receberRespostaPKT rc[" + rc
						+ "] STX recebido ["
						+ Utilitaries.byteArrayToHexString(byteRead) + "]");

				bSTXReceived = true;

				byarrayRspPacket[i++] = 0x02;

			} else if ((bETXReceived == false) && (byteRead[0] == 0x03)) {

				System.out.println("receberRespostaPKT rc[" + rc
						+ "] ETX recebido ["
						+ Utilitaries.byteArrayToHexString(byteRead) + "]");

				bETXReceived = true;

				System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIII [" + i + "]");
				if (byarrayRspPacket.length < i) {

					byarrayRspPacket[i++] = 0x03;

				} else {

					byte[] byteRead2 = new byte[byarrayRspPacket.length + 1];
					/*
					 * Os argumentos “src” e “dest” representam os vetores
					 * origem e destino, respectivamente (em nosso exemplo,
					 * esses vetores chamam-se “a” e “b”). O argumento “srcPos”
					 * é a posição inicial no array origem. O argumento
					 * “destPos” é a posição inicial no array destino. Por fim,
					 * “length” é utilizado para especificarmos o número de
					 * elementos que serão copiados.
					 */

					System.arraycopy(byarrayRspPacket, 0, byteRead2, 0,
							byarrayRspPacket.length);

					byteRead2[byarrayRspPacket.length] = 0x03;

					byarrayRspPacket = new byte[byteRead2.length];

					System.arraycopy(byteRead2, 0, byarrayRspPacket, 0,
							byteRead2.length);
					i++;
				}

			} else if (bETXReceived == true) {

				byteLRCRec = byteRead[0];

				System.out.println("RC = [" + rc + "] LRC foi recebido ["
						+ byteLRCRec + "] ");

				if (byarrayRspPacket.length < i) {
					byarrayRspPacket[i++] = byteLRCRec;
				} else {
					byte[] byteRead2 = new byte[byarrayRspPacket.length + 1];
					System.arraycopy(byarrayRspPacket, 0, byteRead2, 0,
							byarrayRspPacket.length);

					byteRead2[byarrayRspPacket.length] = byteLRCRec;
					byarrayRspPacket = new byte[byteRead2.length];

					System.arraycopy(byteRead2, 0, byarrayRspPacket, 0,
							byteRead2.length);
					i++;
				}

				byteLRCCalc = 0;

				for (i = 1; i < byarrayRspPacket.length - 1; i++) {
					byteLRCCalc ^= byarrayRspPacket[i];
				}

				i++;

				if (byteLRCCalc == byteLRCRec) {
					rc = byarrayRspPacket.length;
					System.out
							.println("recvRspPkt: rc["
									+ rc
									+ "] LRCrcvd["
									+ byteLRCRec
									+ "] LRCcalc["
									+ byteLRCCalc
									+ "] OK Match     i["
									+ i
									+ "] RspPacket["
									+ Utilitaries
											.byteArrayToHexString(byarrayRspPacket)
									+ "] RsppacketLen["
									+ byarrayRspPacket.length + "]");
				} else {

					rc = -216;
					System.err
							.println("recvRspPkt: rc["
									+ rc
									+ "] LRCrcvd["
									+ byteLRCRec
									+ "] LRCcalc["
									+ byteLRCCalc
									+ "] OK Match     i["
									+ i
									+ "] RspPacket["
									+ Utilitaries
											.byteArrayToHexString(byarrayRspPacket)
									+ "] RsppacketLen["
									+ byarrayRspPacket.length + "]");
				}
				
				if (byarrayRspPacket[1] == 'P'){
					System.out.println("POSITIVE RESPONSE["
							+ Utilitaries.byteArrayToHexString(byarrayRspPacket)
							+ "]");
					break;
				}
				else if (byarrayRspPacket[1] == 'N'){
					System.out.println("NEGATIVE RESPONSE["
							+ Utilitaries.byteArrayToHexString(byarrayRspPacket)
							+ "]");
					break;
				}
				else if (byarrayRspPacket[1] == 'R'){
					
					
					System.out.println("================ 5 p ===============================");
					System.out.println("Valor de byarrayPackt[5] = [" + byarrayRspPacket[5] + "]");
					// Status response iMediaStatus = byarrayRspPacket[4] & 0x0F;
					if(byarrayRspPacket[5] != '0') {
						if(iTrackAvailable == 0) {
							System.out.println("Tem trilhas diaponiveis Evento[" + Utilitaries.byteArrayToHexString(byarrayRspPacket) + "]");
							iTrackAvailable = byarrayRspPacket[5]&0x07;
						}
					}else {
						System.out.println("Evento[" + Utilitaries.byteArrayToHexString(byarrayRspPacket) + "]");
					}
					
					System.out.println(" iTrackAvailable [" + iTrackAvailable + "]");
					System.out.println("mediastatus [" + iMediaStatus + "]");
					
					iTrackStatus = byarrayRspPacket[5] & 0x0F;
					System.out.println("trackstatus [" + iTrackStatus + "]");
					iError = 0;
					
					
					
					System.out.println("STATUS RESPONSE["
							+ Utilitaries.byteArrayToHexString(byarrayRspPacket)
							+ "] Ignore....");
					
					bufer.add(byarrayRspPacket);
					
					byarrayRspPacket = new byte[1];
					bSTXReceived = false;
					bETXReceived = false;
					i = 0;
					iAtraso = 0;
					
					enviarConfirmacao(1,true,1000);
				}
  			    else{
  			    	//Pacote errado
  			    	rc = -999;
  			    	return rc;
  			    }

			} else {

				if(byteRead[0] == 0x06)
				{
					
				}
				else
				{
					byte[] byteRead2 = new byte[byarrayRspPacket.length + 1];
					System.arraycopy(byarrayRspPacket, 0, byteRead2, 0,
							byarrayRspPacket.length);
	
					byteRead2[byarrayRspPacket.length] = byteRead[0];
					
					byarrayRspPacket = new byte[byteRead2.length];
					System.arraycopy(byteRead2, 0, byarrayRspPacket, 0,
							byteRead2.length);
					i++;
					
					System.out.println("[0947] byarrayRspPacket["
							+ Utilitaries.byteArrayToHexString(byarrayRspPacket)
							+ "]");
				}

			}
			// System.out.println(" o que veio ["+ byarrayRspPacket[1]+ "]");

		}//While(true)
		

		// System.out.println("byArray posição 1 [" + byarrayRspPacket[1] +
		// "]");
		/*
		 * while(true){ if(byarrayRspPacket[1] == 'R') {
		 * 
		 * }
		 * 
		 * if(byarrayRspPacket[1] == 'P') { desmontarPacote(1); break; }
		 * 
		 * if(byarrayRspPacket[1] == 'N') { break;
		 * 
		 * } }
		 */

		/*
		 * if ((byarrayRspPacket[1] == 'P') || (byarrayRspPacket[1] == 'N')) {
		 * return iError; } else if (byarrayRspPacket[1] == 'R') {
		 * 
		 * if (byarrayRspPacket[4] != '0') { if (getTrackStatus() == 0) {
		 * System.out.println("Tem trilha disponivel"); // receberRespostaPKT(1,
		 * 1000); }
		 * 
		 * } else { System.out.println("Isso é um evento");
		 * 
		 * }
		 * 
		 * byteLRCCalc = 0; // byarrayRspPacket = null; // byteRead = null;
		 * 
		 * } else { System.out
		 * .println("Após o STX esperava receber P, N ou R não recebeu nada[" +
		 * byarrayRspPacket[1] + "]");
		 * 
		 * }
		 */

		if (byarrayRspPacket[1] == 'P') {
			desmontarPacote(1);

		} else if (byarrayRspPacket[1] == 'N') { // Negative response
			iError = (byarrayRspPacket[4] & 0x0F) << 4 + (byarrayRspPacket[5] & 0x0F);

			
			// er0
			// //
			// er1

		} else if (byarrayRspPacket[1] == 'R') {
			System.out.println("===================================== 5 p ===============================");
			System.out.println("Valor de byarrayPackt[5] = [" + byarrayRspPacket[5] + "]");
			// Status response iMediaStatus = byarrayRspPacket[4] & 0x0F;
			if(byarrayRspPacket[5] != '0') {
				if(iTrackAvailable == 0) {
					iTrackAvailable = byarrayRspPacket[5]&0x07;
				}
			}else {
				
			}
			System.out.println(" iTrackAvailable [" + iTrackAvailable + "]");
			System.out.println("mediastatus [" + iMediaStatus + "]");
			iTrackStatus = byarrayRspPacket[5] & 0x0F;
			System.out.println("trackstatus [" + iTrackStatus + "]");
			iError = 0;

			// if (byarrayRspPacket[1] == 'P') {
			// desmontarPacote(1);
			// break;
			// }

		} else {
			rc = -220;
			System.err.println("desmontar pacote rc[" + rc
					+ "] Esperado P,N ou R recebodp[" + byarrayRspPacket[1]
					+ "]");
			return rc;

		}

		return rc;

	}

	private static int enviarConfirmacao(int iProtocol, boolean bSendACK,
			int iTimeout) {
		byte bAckNak = 0x06; // ACK
		int rc = -107;

		try {
			if (bSendACK != true) {
				bAckNak = 0x15; // NAK
			}
			sp.writeByte(bAckNak);
			rc = 0;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("enviarConfirmacao : rc[" + rc + "] Falhou e["
					+ e.getMessage() + "]");
		}
		return rc;

	}

	private static int desmontarPacote(int iProtocol) {
		int rc = -108;
		byarrayRsp = null;
		if (byarrayRspPacket == null) {
			System.err.println("desmontar pacote rc[" + rc
					+ "] falhou pacote veio null");
			return rc;
		} else if (byarrayRspPacket.length == 0) {
			System.err.println("desmontar pacote rc[" + rc
					+ "] falhou pacote veio lengeth == 0");
			return rc;

		} else if ((iProtocol == 1) && (byarrayRspPacket[0] != 0x02)) {
			System.err.println("desmontar pacote rc[" + rc
					+ "] falhou pacote nao veio stx");
			return rc;
		} else if ((iProtocol == 0) && (byarrayRspPacket[0] != 0xF2)) {
			System.err.println("desmontar pacote rc[" + rc
					+ "] falhou pacote nao veio stx");
			return rc;
		} else if ((iProtocol == 1)
				&& (byarrayRspPacket[byarrayRspPacket.length - 2] != 0x03)) {
			System.err.println("desmontar pacote rc[" + rc
					+ "] falhou por que nao tem o etx ["
					+ byarrayRsp[byarrayRspPacket.length - 2] + "] + LRC");
			return rc;
		}

		// Packet começa com STX e terminar com ETX LRC
		if (iProtocol == 1) {
			// STX 'P' cm pm st1 st0 <resposta> ETX LRC
			// STX 'N' cm pm er1 er0 ETX LRC
			// STX 'R' '1' '0' st1 st0 ETX LRC
			System.out.println("TESTE"
					+ Utilitaries.byteArrayToHexString(byarrayRspPacket));
			if (byarrayRspPacket[1] == 'P') { // Positiver response
				byarrayRsp = new byte[byarrayRspPacket.length - 7];
				byte[] b = new byte[1];
				b[0] = byarrayRspPacket[4];
				System.out.println("byarrayRspPacket[4] = ["
						+ Utilitaries.byteArrayToHexString(b) + "]");
				// pega os bits que estao ligados para mostrar os status
				iMediaStatus = byarrayRspPacket[4] & 0x0F;
				iTrackStatus = byarrayRspPacket[5] & 0x0F;
				iError = 0;
				System.arraycopy(byarrayRspPacket, 6, byarrayRsp, 0,
						byarrayRsp.length);
				System.out.println("desmontar pacote [" + iError
						+ "] protocolo [" + iProtocol + "] Resposta["
						+ Utilitaries.byteArrayToHexString(byarrayRsp) + "]");
	
				
			}
			if (byarrayRsp != null) {
				if (iProtocol == 1) {

				}
			}
		} else {
			System.err.println("desmontar pacote rc [" + rc + "] protocolo ["
					+ iProtocol + "] falhou");
		}

		if (iError != 0) {
			iError = -iError;
		}
		return iError;
	}

	private static byte[] getRsp() {
		return byarrayRsp;
	}

	private static void setRsp(byte[] byaRsp) {
		DriverIDC.byarrayRsp = byaRsp;
	}
/*	
	public static int montarPact(byte[] pacote, int protocol) {
		if(protocol == 1) {
			byarrayCmdPacket[0] = 0x02;// STX
			byarrayCmdPacket[1] = 'C'; //'C'
			byarrayCmdPacket[]
		}
	}
	
	public static int processaComando(byte[] comando, int protocol) throws InterruptedException {
		int rc = -1000;
		
		while (bCmdEmProcesso == true) {
			Thread.sleep(1000);
		}
		
		
	}
*/
	private static int processaComando(int iProtocol, byte[] byaCmd,
			byte[] byaCmdParam, int iTimeout) throws InterruptedException, SerialPortException, SerialPortTimeoutException {
		int rc = -109;
		int iCmdTN = 3;
		int iRspTN = 3;

		while (bCmdEmProcesso == true) {
			Thread.sleep(100);
		}
		bCmdEmProcesso = true;
		bSTXReceived = false;

		// montar pacote
		rc = montarPacoteCmd(iProtocol, byaCmd, byaCmdParam, iTimeout);

		System.out.println("VALOR DE byaCmd ["
				+ Utilitaries.byteArrayToHexString(byaCmd) + "]");

		if (rc < 0) {
			bCmdEmProcesso = false;
			System.err.println("montar pacote rc [" + rc + "] falhou");
			return rc;
		}
		// enviar pacote de comando
		while (--iCmdTN > 0) {
			rc = enviarPacoteCmd(byarrayCmdPacket, iTimeout);
			if (rc < 0) { // falha ao enviar pacote
				System.err.println("falha ao enviar o pacote rc [" + rc + "]");
				return rc;

			}

			rc = aguardarSTX(iTimeout);
			if (rc < 0) { // falha deveria recebe ACK OU NAK
				if (rc != 0) { // não recebi NAK
					bCmdEmProcesso = false;
					System.err.println("processar comando : aguarda stx rc ["
							+ rc + "] falhou");
					return rc;

				}

			} else if (rc == 0) {// recebeu ACK
				break;

			} else if (rc == 1) {// recebeu STX
				bSTXReceived = true;
				break;

			}
		}

		while (--iRspTN > 0) {
			rc = receberRespostaPKT(iProtocol, iTimeout);
			if (rc >= 0) { // LRC ok e tiver recebido ACK~

				// if(byarrayRspPacket[1] == 'R') {
				// System.out.println("evento");
				// rc = receberRespostaPKT(iProtocol, iTimeout);
				// }

				rc = enviarConfirmacao(iProtocol, true, 1000);
				break;

			} else if (rc == -210) {// LRC não retornado retornou NAK
				rc = enviarConfirmacao(iProtocol, false, 1000);
				System.err
						.println("processar comando: enviar confirmacao NAK recebido rc ["
								+ rc + "] LRC ");

			} else {// falha ao enviar confirmacao de pacote
				System.err
						.println("processar comando : enviar cornfirmacao falhou ["
								+ rc + "]");
				bCmdEmProcesso = false;
				return rc;

			}

		}
		if (iRspTN == 0) {
			return rc;
		}
		rc = desmontarPacote(iProtocol);
		if (rc < 0) {
			// erro desmontar pacote
			System.err
					.println("processar comando : desmontar pacote falhou rc ["
							+ rc + "]");

		}
		System.out.println("processar comando : rc [" + rc + "] mediaStatus["
				+ getMediaStatus() + "] TrackStatus[" + getTrackStatus()
				+ "] SUCESSO");
		bCmdEmProcesso = false;
		return rc;
	}

	public static int getMediaStatus() {
		return iMediaStatus;
	}

	public static void setMediaStatus(int iMediaStatus) {
		DriverIDC.iMediaStatus = iMediaStatus;
	}

	public static int getTrackStatus() {
		return iTrackStatus;
	}

	public static void setTrackStatus(int iTrackStatus) {
		DriverIDC.iTrackStatus = iTrackStatus;
	}

	public static int getError() {
		return iError;
	}

	public static void setError(int iError) {
		DriverIDC.iError = iError;
	}
	//conversao 
	

}
