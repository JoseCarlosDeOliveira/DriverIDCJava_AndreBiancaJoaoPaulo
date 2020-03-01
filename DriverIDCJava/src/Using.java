import javax.swing.JOptionPane;

import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import Protocol.DriverIDC;
import Utilitaries.Utilitaries;

public class Using {
	private static final char SupervisorRevision = 0;
	
	
	static int rcAbrirComunicacao = 999;
	static int rcFecharComunicacao = 999;
	static int rcSupervisorInitialize = 999;
	static int rcGetSupervisorRevision = 999;
	static int rcAreaSwitch = 999;
	static int rcInitializeICRW = 999;
	static int rcGetStatus = 999;
	static int rcMagTrackRead = 999;
	static int rcEraseTracks = 999;
	static int rcSetLed = 999;
	static int rcGetInfoEMV = 999;
	static int rcGetInfoGIE = 999;
	static int rcGetPassCounter = 999;
	static int rcOpenShutter = 999;
	static int rcGetUserRevision = 999;
	static int rcGetEMVRevision = 999;
	static int rcActivateICC = 999;
	static int rcDeactivateICC = 999;
	static int rcStatusICC = 999;
	static int rcICCcommT0 = 999;
	static int rcWammResetICC = 999;
	static int rcTimerSet = 999;

	static String sRevision = "";
	static String sEmv = "";
	static String area ="";
	static String sTrack = "";
	static String sData = "";
	static String sAtr = "";
	static String sCapdu = "";
	static String sRapdu = "";
	static int iAcao;
    static boolean bSupervisionArea = false;
	public static void main(String[] args) throws IllegalArgumentException,
			InterruptedException, SerialPortException,
			SerialPortTimeoutException {

	

		// ações do case declaradas
		// bAux é a variavel equivalente a sair
		boolean bAux = true;

		// int opção para a escolha do led
		int iOpcaoLed;
		int iOpcaoSetLed;
		int iOpcaoSetFlash;
		int iOpcaoTrack;

		// criado o menu principal com as opcões de escolha
		while (bAux == true) {
			String sX = JOptionPane
					.showInputDialog("========Menu Principal==========" + "\n"
							+ "               Escolha uma opção: " + "\n"
							+ "==============================" + "\n"
							+ "1  - Abrir comunicação      ["
							+ rcAbrirComunicacao
							+ "]\n"
							+ "2  - Fechar comunicação     ["
							+ rcFecharComunicacao
							+ "]\n"
							+ "3  - SupervisorInitialize   ["
							+ rcSupervisorInitialize
							+ "]\n"
							+ "4  - GetSupervisorRevision  ["
							+ rcGetSupervisorRevision
							+ "]  Revision [ "
							+ sRevision.trim()
							+ "]\n" 
							+ "5  - AreaSwitch             ["
							+ rcAreaSwitch
							+ "] area ["
							+area.trim()
							+"] user\n"
							+ "6  - InitializeICRW         ["
							+ rcInitializeICRW
							+ "]\n"
							+ "7  - GetStatus              ["
							+ rcGetStatus
							+ "]\n"
							+ "8  - MagTrackRead           ["
							+ rcMagTrackRead
							+ "] Track [ "
							+ sTrack
							+ "] Data ["
							+ sData
							+ " ]\n"
							+ "9  - EraseTracks            ["
							+ rcEraseTracks
							+ "]\n"
							+ "10 - SetLed                  ["
							+ rcSetLed
							+ "]\n"
							+ "11 - GetInfoEMV             ["
							+ rcGetInfoEMV
							+ "]\n"
							+ "12 - GetInfoGIE             ["
							+ rcGetInfoGIE
							+ "] "
							+ "EMV ["
							+ sEmv.trim()
							+ " ]\n"
							+ "13 - GetPassCounter         ["
							+ rcGetPassCounter
							+ "]\n"
							+ "14 - OpenShutter            ["
							+ rcOpenShutter
							+ "]\n"
							+ "15 - GetUserRevision        ["
							+ rcGetUserRevision
							+ "]\n"
							+ "16 - GetEMVRevision         ["
							+ rcGetEMVRevision
							+ "]\n"
							+ "17 - ActivateICC            ["
							+ rcActivateICC
							+ "] ATR [ "
							+ sAtr.trim()
							+ "]\n"
							+ "18 - DeactivateICC          ["
							+ rcDeactivateICC
							+ "]\n"
							+ "19 - StatusICC              ["
							+ rcStatusICC
							+ "]\n"
							+ "20 - ICCcommT0              ["
							+ rcICCcommT0
							+ "] CAPDU ["
							+ sCapdu
							+ "] RAPDU [ "
							+ sRapdu
							+ "]\n"
							+ "21 - WammResetICC           ["
							+ rcWammResetICC
							+ "]\n"
							+ "22 - TimerSet               ["
							+ rcTimerSet 
							+ "] Acao["
							+iAcao+"]\n" 
							+ "0 - Sair \n");
			if (sX == null) {
				System.exit(0);
			}
			try {
				int iOpcao = Integer.parseInt(sX);

				switch (iOpcao) {
				case 1:
					rcAbrirComunicacao = DriverIDC.abrirComunicacao();
					if (rcAbrirComunicacao < 0) {
						System.out.println("Erro");
						/*
						 * JOptionPane
						 * .showInputDialog("Comunicação iniciada: \n" +
						 * "1 - Voltar \n" + "2 - Fechar comunicação \n" +
						 * "3 - Mandar comando \n");
						 */
					}else{
						rcFecharComunicacao = 999;
					}
					;
					break;
				case 2:
					rcFecharComunicacao = DriverIDC.FecharComunicacao();
					if (rcFecharComunicacao < 0) {
						System.out.println("Porta já estava fechada!");
					} else {
						System.out.println("Deu certo!");
						zerar();
					}
					break;
				case 3:
					rcSupervisorInitialize = DriverIDC.SupervisorInitialize();
					if (rcSupervisorInitialize < 0) {
						System.out
								.println("SupervisorInitialize Comunicacao falhou"
										+ rcSupervisorInitialize);
					}
					break;

				case 4:

					rcGetSupervisorRevision = DriverIDC
							.GetSupervisorRevision(SupervisorRevision);
					if (rcGetSupervisorRevision < 0) {
						System.out
								.println("GetSupervisorRevision Comunicacao falhou"
										+ rcGetSupervisorRevision);
					} else {
						sRevision = "";
						for (int j = 0; j <= DriverIDC.r1.length - 1; j++) {
							sRevision += (char) DriverIDC.r1[j];
						}
					}
					break;

				case 5:
					// esse aqui precisa saber a area que esta sendo usando
					rcAreaSwitch = DriverIDC.AreaSwitch();
					if (rcAreaSwitch < 0) {
						System.out.println("AreaSwitch  Comunicacao falhou"
								+ rcAreaSwitch);
					
					}
					
					break;

				case 6:
					iOpcaoLed = Integer.parseInt(JOptionPane
							.showInputDialog("Digite a opcao     \n"
									+ " 0-  turns LED off        \n"
									+ " 1-  turns Red-LED on     \n"
									+ " 2-  turns Green-LED on   \n"
									+ " 3-  turns G&R-LED on     \n"
									+ " 4-  turns LED of         \n"
									+ " 5-  turns Red-LED on     \n"
									+ " 6-  turns Green-LED on   \n"
									+ " 7-  turns Gren&LED on    \n"));
					
					
				int iOpcaoLatch = Integer.parseInt(JOptionPane
							.showInputDialog("Digite a opcao \n"
									+ " 0-  latch interligada IC commando   \n"
									+ " 1-  latch interligada RES\n"
									+ " 2-  latch interligada IC e ler apenas para trás \n"
									+ " 4-  latch interligada RES quando não a dados magneticos eficazes  \n"
									+ " 8-  latch interligada RES quando alguns dados magneticos podem ser lidos\n"));
				
				
					int iOpcaoFlash = Integer.parseInt(JOptionPane
							.showInputDialog("Digite a opcao \n"
									+ " 0-  No Flash    \n"
									+ " 1-  4s Flash   \n"
									+ " 2-  2s Flash   \n"
									+ " 3-  1s Flash  \n"
									+ " 4-  0.5s Flash  \n"
									+ " 5-  0.25s Flash  \n"));

					
					
					rcInitializeICRW = DriverIDC.InitializeICRW(iOpcaoLed,iOpcaoLatch,iOpcaoFlash);
					if (rcInitializeICRW < 0) {
						System.out.println("InitializeICRW  Comunicacao falhou"
								+ rcInitializeICRW);
					}
					break;

				case 7:
					rcGetStatus = DriverIDC.GetStatus();
					if (rcGetStatus < 0) {
						System.out.println("GetStatus  Comunicacao falhou"
								+ rcGetStatus);
					}
					break;

				case 8:
					iOpcaoTrack = Integer.parseInt(JOptionPane
							.showInputDialog("Digite a opcao \n"
									+ " 1-  Track   \n" + " 2-  Track   \n"
									+ " 3-  Track   \n"));
					if((iOpcaoTrack < 1) || (iOpcaoTrack > 3)) {
						JOptionPane.showMessageDialog(null, "Opção de track invalida!");
						break;
					}
					rcMagTrackRead = DriverIDC.MagTrackRead(iOpcaoTrack);
					if (rcMagTrackRead < 0) {
						System.out.println("MagTrackRead  Comunicacao falhou"
								+ rcMagTrackRead);
						sTrack = "";
						sTrack = String.valueOf(iOpcaoTrack);
					}else{
						sTrack = "";
						sTrack = String.valueOf(iOpcaoTrack);
					}
					break;

				case 9:
					rcEraseTracks = DriverIDC.EraseTracks();
					if (rcEraseTracks < 0) {
						System.out.println("EraseTracks  Comunicacao falhou"
								+ rcEraseTracks);
					}
					break;

				case 10:

					iOpcaoSetLed = Integer.parseInt(JOptionPane
							.showInputDialog("Digite a opcao \n"
									+ " 0-  Turn LEDs Off    \n"
									+ " 1-  Turn RED LEDs ON (GREEN LED is turned off)   \n"
									+ " 2-  Turn GREEN LEDs ON (RED LED is turned off)   \n"
									+ " 3-  Turn RED/GREEN LEDs ON  \n"));
					
					iOpcaoSetFlash= Integer.parseInt(JOptionPane
							.showInputDialog("Digite a opcao \n"
									+ " 0-  No Flash    \n"
									+ " 1-  4s Flash   \n"
									+ " 2-  2s Flash   \n"
									+ " 3-  1s Flash  \n"
									+ " 4-  0.5s Flash  \n"
									+ " 5-  0.25s Flash  \n"));

					
					rcSetLed = DriverIDC.SetLed(iOpcaoSetLed, iOpcaoSetFlash);
					if (rcSetLed < 0) {
						System.out.println("SetLed  Comunicacao falhou"
								+ rcSetLed);
					}
					break;

				case 11:
					rcGetInfoEMV = DriverIDC.GetInfoEMV();
					if (rcGetInfoEMV < 0) {
						System.out.println("GetInfoEMV  Comunicacao falhou"
								+ rcGetInfoEMV);
					}
					break;

				case 12:
					rcGetInfoGIE = DriverIDC.GetInfoGIE();
					if (rcGetInfoGIE < 0) {
						System.out.println("GetInfoGIE  Comunicacao falhou"
								+ rcGetInfoGIE);
					}else {
						sEmv = "";
						for (int j = 13; j <= DriverIDC.r2.length - 2; j++) {
							sEmv += (char) DriverIDC.r2[j];
						}
					}
					break;

				case 13:
					rcGetPassCounter = DriverIDC.GetPassCounter();
					if (rcGetPassCounter < 0) {
						System.out.println("GetPassCounter  Comunicacao falhou"
								+ rcGetPassCounter);
					}
					break;

				case 14:
					rcOpenShutter = DriverIDC.OpenShutter();
					if (rcOpenShutter < 0) {
						System.out.println("OpenShutter  Comunicacao falhou"
								+ rcOpenShutter);
					}
					break;

				case 15:
					rcGetUserRevision = DriverIDC.GetUserRevision();
					if (rcGetUserRevision < 0) {
						System.out
								.println("GetUserRevision  Comunicacao falhou"
										+ rcGetUserRevision);
					}
					break;

				case 16:
					rcGetEMVRevision = DriverIDC.GetEMVRevision();
					if (rcGetEMVRevision < 0) {
						System.out.println("GetEMVRevision  Comunicacao falhou"
								+ rcGetEMVRevision);
					}
					break;

				case 17:
					rcActivateICC = DriverIDC.ActivateICC();
					if (rcActivateICC < 0) {
						System.out.println("ActivateICC  Comunicacao falhou"
								+ rcActivateICC);
					}else{
						sAtr = "";
						sAtr = Utilitaries.byteArrayToHexString(DriverIDC.byaConvToBin);
						
					}
					break;

				case 18:
					rcDeactivateICC = DriverIDC.DeactivateICC();
					if (rcDeactivateICC < 0) {
						System.out.println("DeactivateICC Comunicacao falhou"
								+ rcDeactivateICC);
					}
					break;

				case 19:
					rcStatusICC = DriverIDC.StatusICC();
					if (rcStatusICC < 0) {
						System.out.println("StatusICC Comunicacao falhou"
								+ rcStatusICC);
					}
					break;

				case 20:
					byte[] CAPDU = new byte[19];
					CAPDU[0] = 0x00;
					CAPDU[1] = (byte) 0xA4;
					CAPDU[2] = 0x04;
					CAPDU[3] = 0x02;
					CAPDU[4] = 0x0E;
					CAPDU[5] = 0x31;
					CAPDU[6] = 0x50;
					CAPDU[7] = 0x41;
					CAPDU[8] = 0x59;
					CAPDU[9] = 0x2E;
					CAPDU[10] = 0x53;
					CAPDU[11] = 0x59;
					CAPDU[12] = 0x53;
					CAPDU[13] = 0x2E;
					CAPDU[14] = 0x44;
					CAPDU[15] = 0x44;
					CAPDU[16] = 0x46;
					CAPDU[17] = 0x30;
					CAPDU[18] = 0x31;
					
					rcICCcommT0 = DriverIDC.ICCcommT0(CAPDU);
					if (rcICCcommT0 < 0) {
						System.out.println("ICCcommT0 Comunicacao falhou"
								+ rcICCcommT0);
					}else{
						sCapdu = "";
						sRapdu = "";
						//Utilitaries.CovBinASCIIhexSankyo(CAPDU);
						sCapdu = Utilitaries.byteArrayToHexString(CAPDU);
						sRapdu = DriverIDC.sRAPDU;
						//for(int j = 0; j < CAPDU.length; j++) {
							//sCapdu += CAPDU[j];
						//}
					}
					break;

				case 21:
					rcWammResetICC = DriverIDC.WammResetICC();
					if (rcWammResetICC < 0) {
						System.out.println("WammResetICC Comunicacao falhou"
								+ rcWammResetICC);
					}
					break;

				case 22:
					// não era necessario fazer timer set
					iAcao = Integer.parseInt(JOptionPane
							.showInputDialog("Digite a opcao \n"
									+ "h    \n"
									));
					rcTimerSet = DriverIDC.TimerSet();
					if (rcTimerSet < 0) {
						System.out.println("TimerSet Comunicacao falhou"
								+ rcTimerSet);
					}
					break;
				case 0:
					System.out.println("Sair!");
					bAux = false;
					break;
				default:
					JOptionPane.showMessageDialog(null, "Opção invalida!");
					break;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Digite apenas valores numéricos");
			}
		}

		/*
		 * int resultado = Protocol.AbrirComunicacao(); if (resultado > 0) {
		 * System.out.println("Abriu comunicação!"); } else {
		 * System.out.println("Falha!"); }
		 */

	}
	
	public static void zerar() {
		rcAbrirComunicacao = 999;
		rcSupervisorInitialize = 999;
		rcGetSupervisorRevision = 999;
		rcAreaSwitch = 999;
		rcInitializeICRW = 999;
		rcGetStatus = 999;
		rcMagTrackRead = 999;
		rcEraseTracks = 999;
		rcSetLed = 999;
		rcGetInfoEMV = 999;
		rcGetInfoGIE = 999;
		rcGetPassCounter = 999;
		rcOpenShutter = 999;
		rcGetUserRevision = 999;
		rcGetEMVRevision = 999;
		rcActivateICC = 999;
		rcDeactivateICC = 999;
		rcStatusICC = 999;
		rcICCcommT0 = 999;
		rcWammResetICC = 999;
		rcTimerSet = 999;

		sRevision = "";
		sEmv = "";
		sTrack = "";
		sData = "";
		area ="";
	    sAtr = "";
	}

}
