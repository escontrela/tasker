package com.davidpe.tasker.application.mcp.config;

import com.davidpe.tasker.application.mcp.tool.TaskMcpTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfig {

  @Bean
  ToolCallbackProvider toolProvider(TaskMcpTool taskMcpTool) {
    return MethodToolCallbackProvider.builder().toolObjects(taskMcpTool).build();
  }
}
