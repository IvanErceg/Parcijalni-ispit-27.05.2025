import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DataSource dataSource = createDataSource();

        System.out.println("Odaberite opciju:");
        System.out.println("1 - unesi novog polaznika");
        System.out.println("2 - unesi novi program obrazovanja");
        System.out.println("3 - upiši polaznika na program obrazovanja");
        System.out.println("4 - prebaci polaznika iz jednog u drugi program obrazovanja");
        System.out.println("5 - ispisi ime, prezime polaznika, naziv programa obrazovanja, broj CSVET bodova");
        System.out.println("6 - kraj");

        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Uspješno ste spojeni na bazu podataka!");
            Scanner scanner = new Scanner(System.in);
            int unos;

            do {
                unos = scanner.nextInt();
                scanner.nextLine();

                switch (unos) {
                    case 1:
                        // Unos novog polaznika
                        System.out.print("Unesite ime polaznika: ");
                        String ime = scanner.nextLine();
                        System.out.print("Unesite prezime polaznika: ");
                        String prezime = scanner.nextLine();

                        try (CallableStatement cstmt = connection.prepareCall("{call DodajPolaznika(?, ?)}")) {
                            cstmt.setString(1, ime);
                            cstmt.setString(2, prezime);
                            cstmt.execute();
                            System.out.println("Polaznik uspješno dodan.");
                        } catch (SQLException e) {
                            System.out.println("Greška prilikom dodavanja polaznika: " + e.getMessage());
                        }
                        break;

                    case 2:
                        // Unos novog programa obrazovanja
                        System.out.print("Unesite naziv programa: ");
                        String nazivPrograma = scanner.nextLine();
                        System.out.print("Unesite broj CSVET bodova: ");
                        int bodovi = scanner.nextInt();

                        try (CallableStatement cstmt = connection.prepareCall("{call DodajProgramObrazovanja(?,?)}")) {
                            cstmt.setString(1, nazivPrograma);

                            cstmt.setInt(2, bodovi);
                            cstmt.execute();
                            System.out.println("Program obrazovanja uspješno dodan.");
                        } catch (SQLException e) {
                            System.out.println("Greška prilikom dodavanja programa: " + e.getMessage());
                        }
                        break;

                    case 3:
                        // Upis polaznika na program
                        System.out.print("Unesite ID polaznika: ");
                        int idPolaznik = scanner.nextInt();
                        System.out.print("Unesite ID programa: ");
                        int idProgram = scanner.nextInt();

                        try (CallableStatement cstmt = connection.prepareCall("{call UpisiPolaznikaNaProgram(?, ?)}")) {
                            cstmt.setInt(1, idPolaznik);
                            cstmt.setInt(2, idProgram);
                            cstmt.execute();
                            System.out.println("Polaznik uspješno upisan na program.");
                        } catch (SQLException e) {
                            System.out.println("Greška prilikom upisa polaznika: " + e.getMessage());
                        }
                        break;

                    case 4:
                        // Prebacivanje polaznika na drugi program
                        System.out.print("Unesite ID polaznika: ");
                        int polaznikId = scanner.nextInt();

                        System.out.print("Unesite novi ID programa: ");
                        int noviProgram = scanner.nextInt();

                        try (CallableStatement cstmt = connection.prepareCall("{call PrebaciPolaznika(?, ?)}")) {
                            cstmt.setInt(1, polaznikId);

                            cstmt.setInt(2, noviProgram);
                            cstmt.execute();
                            System.out.println("Polaznik uspješno prebačen na novi program.");
                        } catch (SQLException e) {
                            System.out.println("Greška prilikom prebacivanja polaznika: " + e.getMessage());
                        }
                        break;

                    case 5:
                        System.out.print("Unesite ID programa za prikaz polaznika: ");
                        int idPrograma = scanner.nextInt();

                        try (CallableStatement cstmt = connection.prepareCall("{call IspisiPolaznikeSaProgramima(?)}")) {
                            cstmt.setInt(1, idPrograma);
                            try (ResultSet rs = cstmt.executeQuery()) {
                                System.out.println("Polaznici i programi:");
                                System.out.println("Ime                 Prezime                   Program                    Bodovi");
                                System.out.println("-------------------------------------------------------------------------------");

                                while (rs.next()) {
                                    System.out.printf("%-20s %-20s %-30s %-10d\n",
                                            rs.getString("Ime"),
                                            rs.getString("Prezime"),
                                            rs.getString("NazivPrograma"),
                                            rs.getInt("CSVETBodovi"));
                                }
                            }
                        } catch (SQLException e) {
                            System.out.println("Greška prilikom dohvaćanja podataka: " + e.getMessage());
                        }
                        break;

                    case 6:
                        System.out.println("Kraj programa.");
                        break;

                    default:
                        System.out.println("Nepoznata opcija. Molimo pokušajte ponovo.");
                }
            } while (unos != 6);

            scanner.close();
        } catch (SQLException e) {
            System.err.println("Greška prilikom spajanja na bazu podataka");
            e.printStackTrace();
        }
    }

    private static DataSource createDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("JavaAdv");
        ds.setUser("sa");
        ds.setPassword("SQL");
        ds.setEncrypt(false);
        return ds;
    }
}