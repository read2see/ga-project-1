import com.acme.models.Customer;
import com.acme.models.Person;
import com.acme.utils.Hash;

public class Main {
    public static void main(String[] args) {

        var acu = new Customer("Ali", "Elia", "ex@m.co", "xyz");

        System.out.println(acu.toJson());


    }
}
