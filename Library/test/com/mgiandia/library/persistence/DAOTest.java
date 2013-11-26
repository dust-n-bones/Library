package com.mgiandia.library.persistence;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mgiandia.library.domain.Book;
import com.mgiandia.library.domain.Borrower;
import com.mgiandia.library.domain.ISBN;
import com.mgiandia.library.domain.Item;
import com.mgiandia.library.domain.Loan;
import com.mgiandia.library.persistence.Initializer;
import com.mgiandia.library.persistence.JPAUtil;



/**
 * Κλάση ελέγχου για τις βασικές πράξεις των αντικειμένων πρόσβασης δεδομένων
 * @author Νίκος Διαμαντίδης
 *
 */

public class DAOTest {



    private Initializer dataHelper;
    private EntityManager entityManager;
    
    private static final int INITIAL_BORROWER_COUNT = 2;
    private static final int INITIAL_ITEM_COUNT = 5;    
    private static final int INITIAL_LOAN_COUNT = 0;
    
    private static final int BORROWER_NO_FOR_NEW_LOAN = 1;
    private static final int ITEM_NO_FOR_NEW_LOAN = 1;
    
    
    @Before
    public void bef() {
    	entityManager = JPAUtil.createEntityManager();
    }
    
    public void setUp() {                

        dataHelper.prepareData();
    }
 
    
    public void setUpJpa() {
        dataHelper = new Initializer();
        setUp();
    }
    
    @After
    public void closeEm() {
    	if (entityManager != null) {
    		if (entityManager.isOpen()) {
    	    	entityManager.close();    			
    		}
    	}

    }

    
    @Test
    public void findExistingBorrowerJpa() { 
        setUpJpa();
        Borrower borrower = entityManager.find(Borrower.class,2);
        Assert.assertEquals("Νίκος", borrower.getFirstName());                       
    }
    
    



    @Test
    public void findNonExistingBorrowerJpa() { 
        setUpJpa();
        Borrower borrower = entityManager.find(Borrower.class,4711);
        Assert.assertNull(borrower);    
    }


    
    @Test
    public void listAllBorrowersJpa() { 
        setUpJpa();
        List<Borrower> allBorrowers = findAllBorrowers();
        Assert.assertEquals(INITIAL_BORROWER_COUNT, allBorrowers.size());        
      
    }



    
    
   
    
    @Test
    public void saveBorrowerJpa() { 
        setUpJpa();
        Borrower borrower = new Borrower(5000, "Giannis", "Martinopoulos", null, null, null);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(borrower);
        tx.commit();
        
        Assert.assertEquals(INITIAL_BORROWER_COUNT + 1, findAllBorrowers().size());
        Assert.assertNotNull(entityManager.find(Borrower.class,borrower.getBorrowerNo()));
        Assert.assertTrue(findAllBorrowers().contains(borrower));
    }
    
    


    
    @Test
    public void deleteBorrowerJpa() { 
        setUpJpa();
        List<Borrower> allBorrowers = findAllBorrowers();
        Borrower borrower = allBorrowers.get(0);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.remove(borrower);
        tx.commit();
        
        allBorrowers = findAllBorrowers();
        
        Assert.assertEquals(INITIAL_BORROWER_COUNT - 1, allBorrowers.size());
        
    }

    

     /**
     * Αναζήτηση αντιτύπου που υπάρχει στη βάση δεδομένων
     */   
    @Test
    public void findExistingItemJpa() { 
        setUpJpa();
        String EXPECTED_ISBN_FROM_ITEM = "1";
        
        Item item = entityManager.find(Item.class, 1);
        Assert.assertEquals(EXPECTED_ISBN_FROM_ITEM , item.getBook().getIsbn().getValue());
    }



    
    /**
     * Αναζήτηση αντιτύπου που δεν υπάρχει στη βάση δεδομένων
     */
    @Test
    public void findNonExistingItemJpa() { 
        setUpJpa();
        Item item = entityManager.find(Item.class, 4711);
        Assert.assertNull(item);
  
    }

    
    /**
     * Κατάλογος όλων των αντιτύπων
     */
    @Test
    public void listAllItemsItemJpa() { 
        setUpJpa();
        List<Item> allItems = finAllItems();       
        Assert.assertEquals(INITIAL_ITEM_COUNT, allItems.size());         
    }
 
    

    
    /**
     * Αποθήκευση αντιτύπου
     */
    @Test
    public void saveItemJpa() { 
        setUpJpa();
        Book book = new Book("One Title", new ISBN("9999"), null, 0, null);
        Item item = new Item(10);
        item.setBook(book);
        
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(item);
        tx.commit();
        
        List<Item> allItems = finAllItems();
        Assert.assertEquals(INITIAL_ITEM_COUNT + 1, allItems.size());
        Assert.assertNotNull(entityManager.find(Item.class, item.getItemNumber()));
        Assert.assertTrue(allItems.contains(item));      
    }      
    

    /**
     * Διαγραφή αντιτύπου
     */
    @Test
    public void deleteItemJpa() { 
        setUpJpa();
        Item item = finAllItems().get(0);
        item.setBook(null);
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.remove(item);
        tx.commit();
        
        List<Item> allItems = finAllItems();
        Assert.assertEquals(INITIAL_ITEM_COUNT - 1, allItems.size());
        Assert.assertNull(entityManager.find(Item.class, item.getItemNumber()));
        Assert.assertFalse(allItems.contains(item));        
    }      
    
   
  
    

   
    
    /**
     * Αποθήκευση δανεισμού    
     */
    @Test
    public void saveLoanJpa() { 
        setUpJpa();
        Loan loan = CreateNewLoan();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(loan);
        tx.commit();
        List<Loan> allLoans = findAllLoans();
        Assert.assertEquals(INITIAL_LOAN_COUNT + 1, allLoans.size());

    }      
    
    

   


    
    
    private Loan CreateNewLoan() {
        Borrower borrower = entityManager.find(Borrower.class, BORROWER_NO_FOR_NEW_LOAN); 
        Item item = entityManager.find(Item.class, ITEM_NO_FOR_NEW_LOAN);
        Loan loan = item.borrow(borrower);
        return loan;
    }
    
    
	@SuppressWarnings("unchecked")
	private List<Borrower> findAllBorrowers() {
		List<Borrower> allBorrowers = entityManager
        	.createQuery("select b from Borrower b")
        	.getResultList();
		return allBorrowers;
	}
   
	@SuppressWarnings("unchecked")
	private List<Item> finAllItems() {
		List<Item> allItems = entityManager
			.createQuery("select i from Item i")
			.getResultList();
		return allItems;
	}

	@SuppressWarnings("unchecked")
	private List<Loan> findAllLoans() {
		List<Loan> allLoans = entityManager
			.createQuery("select l from Loan l")
			.getResultList();
		return allLoans;
	}
}
