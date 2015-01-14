package com.appspot.potlachkk.integration.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;

import com.appspot.potlachkk.auth.LoginStatusBuilder;
import com.appspot.potlachkk.auth.LoginStatusBuilder.LoginStatusCode;
import com.appspot.potlachkk.client.ChainSvcApi;
import com.appspot.potlachkk.client.GiftSvcApi;
import com.appspot.potlachkk.client.ImageSvcApi;
import com.appspot.potlachkk.client.LoginSvcApi;
import com.appspot.potlachkk.client.UserSvcApi;
import com.appspot.potlachkk.model.Chain;
import com.appspot.potlachkk.model.Gift;
import com.appspot.potlachkk.repository.GiftRepository;
import com.appspot.potlachkk.video.TestData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Test Suite using Retrofit and UnsafeHttpsClient
 * (we may not need to use Https client since
 * the test runs locally on Http only)
 *
 * The approach is based on examples provided by
 * Dr. C. Jules White
 *
 */

public class LocalClientApiTest {

	private final String TEST_URL = "http://localhost:8080";
	private final static String CONFIG_URL = "http://localhost:8080/config";
	private final static String DEL_CONFIG_URL = "http://localhost:8080/delconfig";
	
	private static final Logger log = Logger.getLogger(GiftRepository.class.getName());
	
	private final ApacheClient myClient = new ApacheClient(UnsafeHttpsClient.createUnsafeClient());
	
	Gson gson = new GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    .create(); 
	
	private LoginSvcApi loginService = new RestAdapter.Builder()
		.setClient(myClient)
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(LoginSvcApi.class);

	private GiftSvcApi giftService = new RestAdapter.Builder()
		.setClient(myClient)
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL)
		.setConverter(new GsonConverter(gson))
		.build()
		.create(GiftSvcApi.class);

	private ChainSvcApi chainService = new RestAdapter.Builder()
		.setClient(myClient)
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL)
		.setConverter(new GsonConverter(gson))
		.build()
		.create(ChainSvcApi.class);
 
	private UserSvcApi userService = new RestAdapter.Builder()
		.setClient(myClient)
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL)
		.setConverter(new GsonConverter(gson))
		.build()
		.create(UserSvcApi.class);

	private ImageSvcApi imgService = new RestAdapter.Builder()
		.setClient(myClient)
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL)
		.setConverter(new GsonConverter(gson))
		.build()
		.create(ImageSvcApi.class);

	
	
	private Gift gift = TestData.getRandomGift();

	
		
	//create some data for tests
	//details in ConfigSVC
	@BeforeClass
    public static void setUp() {
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(CONFIG_URL);
		try {
			HttpResponse response = client.execute(request);
		} catch (Exception e) {
			fail("Config failed");
		}
    	
    }
	
	
	//login as "user1" before every test
	@Before
	public void testLogin(){
		//we are logged in
		loginService.login("user1", "pass1");
	}
	
	//logout "user1" after every test
	@After
	public void testLogout(){
		loginService.logout();
	}
	
	
	//try to access restricted resources after logout
	@Test
	public void testLoginLogout() throws Exception {
				
		//we are logged in
		LoginStatusBuilder.LoginStatus ls1 = loginService.login("user2", "pass2");
		assertTrue(ls1.getCode().equals(LoginStatusCode.LOGIN_OK));
		
		chainService.getChainList();
		
		//now let's log out
		LoginStatusBuilder.LoginStatus ls2 = loginService.logout();
		
		try{
			chainService.getChainList();
			fail("We shouldn't make it here if logout works!");
		}catch(Exception e){
			assertTrue(true);
		}
		
	}
	
	/*
	@Test
	public void testLoginWrong() throws Exception{
		LoginStatusBuilder.LoginStatus ls1 = loginService.login("user2xxx", "pass2");
	}
	*/
	
	//add gift with no chain (it should create new chain as well)
	@Test
	public void testAddGiftNoChain() throws Exception{
		//add gift without chainId - should create new chain
		//return its id
		
		Gift g = new Gift("NoChainTestPic", "NoChainTestTitle", "NoChainTestText");
		Long chainId = giftService.addGift(g);
		assertTrue(chainId!=null);
		
		Chain ch = chainService.getChain(chainId);
		assertTrue(ch.getGifts().contains(g));
	}
	
	//try to add gift to existing chain
	@Test
	public void testAddGiftToChain() throws Exception{
		
		//add gift to existing chain
		List<Chain> chainList = (List<Chain>) chainService.getChainList();
		
		Chain ch = chainList.get(0);
		
		log.info("chid:" + ch.getId());
		
		Gift g = new Gift("ChainTestPic", "ChainTestTitle", "ChainTestText");
		g.setChainId(ch.getId());
		
		Long chainId = giftService.addGift(g);
		
		
		log.info("chid2:" + chainId);
		assertTrue(chainId.equals(ch.getId()));
		
		Chain ch2 = chainService.getChain(chainId);
		assertTrue(ch2.getGifts().contains(g));
		
		//test if chain's updateDate is change after addition of new
		assertTrue(ch.getUpdateDate().compareTo(ch2.getUpdateDate()) < 0);
	}
	
	//try to add gift to no existing chain
	@Test
	public void testAddGiftToWrongChain() throws Exception{
		
		Gift g = new Gift("ChainTestPic", "ChainTestTitle", "ChainTestText");
		g.setChainId(new Long(1));
		
		try{
			giftService.addGift(g);
			fail("We should not manage to add gift with wrong chain id.");
		}
		catch(Exception e){
			assertTrue(true);
		}
	}
	
	//Check if it is possible to add a gift with user different
	//than the session owner
	@Test
	public void testAddGifWrongUsername() throws Exception {
		
		//let's add gift with wrong username
		Gift g = new Gift("picture", "title", "text");
		g.setUsername("bad_user");
		
		try{
			giftService.addGift(g);
			fail("Gift with wrong username added!");
		}catch(Exception e){
			assertTrue(true);
		}
		
	}
	
	@Test
	public void testGetGiftById() throws Exception{
		
		//get first appearing chain
		//at this moment we should have schains with gifts
		List<Chain> chainList = (List<Chain>) chainService.getChainList();
		Chain ch = chainList.get(0);
		
		//get fits gift form that chain
		List<Gift> giftList = ch.getGifts();
		
		//try to get the first gift by its id
		Gift g = giftList.get(0);
		Gift gg = giftService.getGiftById(g.getId());
		
		assertTrue(g.equals(gg));
		assertTrue(g.getId().equals(gg.getId()));
	}
	
	@Test
	public void testGetGiftNoID() throws Exception{

		//try to get gift with null id
		try{
			giftService.getGiftById(null);
			fail("We should get an exception");
		}
		catch (Exception e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetGiftNoExisting() throws Exception{
		//try to get gift with null id
		try{
			giftService.getGiftById(new Long(1));
			fail("We should get an exception");
		}
		catch (Exception e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testDeleteGift() throws Exception{
		
		//create new gift - should create a new chain
		Gift g1 = new Gift("DeleteGiftTestPic1", "DeleteGiftTestTitle1", "DeleteGiftTestText1");
		Long chainId1 = giftService.addGift(g1);
		
		//add another gift to this chain
		Gift g2 = new Gift("DeleteGiftTestPic2", "DeleteGiftTestTitle2", "DeleteGiftTestText2");
		g2.setChainId(chainId1);
		Long chainId2 = giftService.addGift(g2);
		
		//both chain ids should has the same value
		assertTrue(chainId1.equals(chainId2));
		
		//get the chain and its gifts
		Chain ch = chainService.getChain(chainId1);
		List<Gift> gifts = ch.getGifts();
		
		//chain should have only two gifts
		assertTrue(gifts.size()==2);
		
		//remove one gift
		Gift gg1 = gifts.get(0);
		giftService.deleteGiftById(gg1.getId());
		
		//get the chain again
		//it should have only one gift left
		ch = chainService.getChain(chainId1);
		gifts = ch.getGifts();
		assertTrue(gifts.size()==1);
		
		//delete the lasting gift
		Gift gg2 = gifts.get(0);
		giftService.deleteGiftById(gg2.getId());
		
		//check if the two gifts were deleted
		try{
			giftService.getGiftById(gg1.getId());
			fail("This gift should not exist anymore!");
		}
		catch(Exception e){
			assertTrue(true);
		}
		
		try{
			giftService.getGiftById(gg2.getId());
			fail("This gift should not exist anymore!");
		}
		catch(Exception e){
			assertTrue(true);
		}
		
		//check if the chain is deleted
		try{
			chainService.getChain(chainId1);
			fail("This chain should not exist anymore!");
		}
		catch(Exception e){
			assertTrue(true);
		}
	}
	
	
	//get gift by title
	@Test
	public void testFindGiftByTitle() throws Exception{
		
		String title = "FindGiftByTitleTitle1";
		
		//create new gift
		Gift g1 = new Gift("FindGiftByTitlePics1", title, "FindGiftByTitleText1");
		giftService.addGift(g1);
		
		//try to find gift
		try{
			List<Gift> giftList = (List<Gift>) giftService.findByTitle(title);
			
			//it should find exaclty one gift
			Gift g2 = giftList.get(0);
			
			//and it should be g1
			assertTrue(g1.equals(g2));
		}
		catch(Exception e){
			fail("We should get one gift");
		}
	}
	
	@Test
	public void testLikeGift() throws Exception{
		//we are logged in
		//LoginStatusBuilder.LoginStatus ls1 = loginService.login("user2", "pass2");
		//assertTrue(ls1.getCode().equals(LoginStatusCode.LOGIN_OK));
		
		//create new gift
		Gift g1 = new Gift("LikeTestPics1", "LikeTestTitle", "LikeTestText1");
		Long chainId = giftService.addGift(g1);
		
		//try to find gift
		try{
			Chain ch = chainService.getChain(chainId);
			List<Gift> giftList = ch.getGifts();
			
			//it should find exactly one gift
			g1 = giftList.get(0);
		}
		catch(Exception e){}
		
		Long id = g1.getId();
		
		//like gift
		giftService.like(id);
		
		//get gift again and check if it has one like
		Gift gg1 = giftService.getGiftById(id);
		assertTrue(gg1.getLikeCount()==1);
		
		//check if current user "user1" in on gift's like list
		assertTrue(gg1.getLikedBy().contains("user1"));
		
		//check if gifts id appers on the list of gifts liked by user
		//TODO: User usr = loginService.
		
		//log in user2
		LoginStatusBuilder.LoginStatus ls1 = loginService.login("user2", "pass2");
		
		//user2 like gift
		giftService.like(id);
		
		//get gift again and check if it has two likes
		gg1 = giftService.getGiftById(id);
		assertTrue(gg1.getLikeCount()==2);
				
		//check if current user "user2" in on gift's like list
		assertTrue(gg1.getLikedBy().contains("user2"));
		
		//user2 like the same gift again = unlike
		giftService.like(id);
		
		//get gift again and check if it has one like again
		gg1 = giftService.getGiftById(id);
		assertTrue(gg1.getLikeCount()==1);
						
		//check if current user "user2"  disappeared from gift's like list
		assertTrue(!gg1.getLikedBy().contains("user2"));
		
		//unlike by user2
		giftService.like(id);
	}
	
	@Test
	public void testTopGivers() throws Exception {
		try{
			userService.getTopGivers();
			assertTrue(true);
		}
		catch(Exception e){
			assertTrue(false);
		}
	}

	@Test
	public void testFlagGift() throws Exception{
		//TODO zmienic opisy
		
		//we are logged in
		//LoginStatusBuilder.LoginStatus ls1 = loginService.login("user2", "pass2");
		//assertTrue(ls1.getCode().equals(LoginStatusCode.LOGIN_OK));
		
		//create new gift
		Gift g1 = new Gift("FlagTestPics1", "FlagTestTitle", "FlagTestText1");
		Long chainId = giftService.addGift(g1);
		
		//try to find gift
		try{
			Chain ch = chainService.getChain(chainId);
			List<Gift> giftList = ch.getGifts();
			
			//it should find exactly one gift
			g1 = giftList.get(0);
		}
		catch(Exception e){}
		
		Long id = g1.getId();
		
		//like gift
		giftService.flag(id);
		
		//get gift again and check if it has one like
		Gift gg1 = giftService.getGiftById(id);
		assertTrue(gg1.getFlagCount()==1);
		
		//check if current user "user1" in on gift's like list
		assertTrue(gg1.getFlaggedBy().contains("user1"));
		
		//check if gifts id appers on the list of gifts liked by user
		//TODO: User usr = loginService.
		
		//log in user2
		LoginStatusBuilder.LoginStatus ls1 = loginService.login("user2", "pass2");
		
		//user2 like gift
		giftService.flag(id);
		
		//get gift again and check if it has two likes
		gg1 = giftService.getGiftById(id);
		assertTrue(gg1.getFlagCount()==2);
				
		//check if current user "user2" in on gift's like list
		assertTrue(gg1.getFlaggedBy().contains("user2"));
		
		//user2 like the same gift again = unlike
		giftService.flag(id);
		
		//get gift again and check if it has one like again
		gg1 = giftService.getGiftById(id);
		assertTrue(gg1.getFlagCount()==1);
						
		//check if current user "user2"  disappeared from gift's like list
		assertTrue(!gg1.getFlaggedBy().contains("user2"));
		
		//unlike by user2
		giftService.flag(id);
	}

	@Test 
	public void testImageUpload() throws Exception {
		
		File testImg = new File("src/test/resource/imgtest.jpg");
		
		String contentType = Files.probeContentType(testImg.toPath());
		TypedFile testFile = new TypedFile(contentType, testImg);
		
		Long id = imgService.addImage(testFile);
		
		
		File testImg2 = new File("src/test/resource/imgtest.png");
		
		String contentType2 = Files.probeContentType(testImg2.toPath());
		TypedFile testFile2 = new TypedFile(contentType2, testImg2);
		imgService.addImage(testFile2);
		
		
		

		File testImg3 = new File("src/test/resource/imgtest.gif");
		
		String contentType3 = Files.probeContentType(testImg3.toPath());
		TypedFile testFile3 = new TypedFile(contentType3, testImg3);
		imgService.addImage(testFile3);
		
		//imgService.getImageById(id);
		
		
	}
	
	@AfterClass
	public static void tearDown() {
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(DEL_CONFIG_URL);
		try {
			HttpResponse response = client.execute(request);
		} catch (Exception e) {
			fail("Config delete failed");
		}
		
		System.out.println("Done!");
	}
}
