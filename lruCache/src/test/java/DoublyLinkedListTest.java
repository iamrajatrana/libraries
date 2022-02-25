import com.dummy.cache.DoubleLinkedList;
import com.dummy.cache.Node;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoublyLinkedListTest {

    @Test
    void whenCreated_thenSizeIsZero() {
        DoubleLinkedList dl = new DoubleLinkedList();
        assertEquals(0, dl.size());
    }

    @Test
    void whenInsertFromLast_thenSizeIsMoreThanZero() {
        DoubleLinkedList dl = new DoubleLinkedList();
        dl.addLast(new Node( '1', '1'));
        dl.addLast(new Node( '1', '1'));
        dl.addLast(new Node( '1', '1'));
        dl.addLast(new Node( '1', '1'));

        assertEquals(4, dl.size());
    }

}
