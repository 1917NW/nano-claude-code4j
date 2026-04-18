import com.lxy.common.JsonKeyConverter;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonKeyConverter {

    @Test
    public void testCamelToUnderlineJson() {
        String json = "{\"userName\":\"Tom\",\"userAge\":18,\"addressList\":[{\"cityName\":\"Beijing\",\"zipCode\":\"100000\"}],\"URLValue\":\"https://example.com\"}";

        String result = JsonKeyConverter.camelToUnderlineJson(json);

        Assert.assertTrue(result.contains("\"user_name\""));
        Assert.assertTrue(result.contains("\"user_age\""));
        Assert.assertTrue(result.contains("\"address_list\""));
        Assert.assertTrue(result.contains("\"city_name\""));
        Assert.assertTrue(result.contains("\"zip_code\""));
        Assert.assertTrue(result.contains("\"url_value\""));
    }

    @Test
    public void testUnderlineToCamelJson() {
        String json = "{\"user_name\":\"Tom\",\"user_age\":18,\"address_list\":[{\"city_name\":\"Beijing\",\"zip_code\":\"100000\"}]}";

        String result = JsonKeyConverter.underlineToCamelJson(json);

        System.out.println(result);

        Assert.assertTrue(result.contains("\"userName\""));
        Assert.assertTrue(result.contains("\"userAge\""));
        Assert.assertTrue(result.contains("\"addressList\""));
        Assert.assertTrue(result.contains("\"cityName\""));
        Assert.assertTrue(result.contains("\"zipCode\""));
    }
}
