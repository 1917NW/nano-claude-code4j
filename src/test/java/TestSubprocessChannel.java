import cn.hutool.json.JSONObject;
import com.lxy.mcp.SubprocessChannel;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSubprocessChannel {

    @Test
    public void shouldSendMessageToChildProcessAndReceiveStdout() throws Exception {
        List<String> command = Arrays.asList(
                "python3",
                "-u",
                "-c",
                "import sys; " +
                        "print('child ready', flush=True); " +
                        "for line in sys.stdin: " +
                        " text=line.strip(); " +
                        " print('echo:' + text, flush=True)"
        );

        try (SubprocessChannel channel = SubprocessChannel.start(command)) {
            Assert.assertEquals("child ready", channel.readLine());

            channel.sendMessage("hello");
            Assert.assertEquals("echo:hello", channel.readLine());

            channel.sendMessage("world");
            Assert.assertEquals("echo:world", channel.readLine());

            Assert.assertTrue(channel.isAlive());
        }
    }

    @Test
    public void shouldPassEnvironmentVariablesToChildProcess() throws Exception {
        List<String> command = Arrays.asList(
                "python3",
                "-u",
                "-c",
                "import os; print(os.environ.get('MCP_TEST_ENV', ''), flush=True)"
        );

        Map<String, String> env = new HashMap<>();
        env.put("MCP_TEST_ENV", "hello-env");

        try (SubprocessChannel channel = SubprocessChannel.start(command, env)) {
            Assert.assertEquals("hello-env", channel.readLine());
        }
    }

    @Test
    public void shouldReadJsonRpcMessageFromChildProcess() throws Exception {
        List<String> command = Arrays.asList(
                "python3",
                "-u",
                "-c",
                "print('{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":{\"ok\":true}}', flush=True)"
        );

        try (SubprocessChannel channel = SubprocessChannel.start(command)) {
            JSONObject message = channel.readJsonMessage();

            Assert.assertNotNull(message);
            Assert.assertEquals("2.0", message.getStr("jsonrpc"));
            Assert.assertEquals(Integer.valueOf(1), message.getInt("id"));
            Assert.assertTrue(message.getJSONObject("result").getBool("ok"));
        }
    }
}
