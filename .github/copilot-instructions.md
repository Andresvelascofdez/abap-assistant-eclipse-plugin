<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# ABAP Programming Assistant Eclipse Plugin

This is an Eclipse Plugin project for SAP ABAP development assistance with ChatGPT integration.

## Project Structure Guidelines

- This is an Eclipse RCP/Plugin project using OSGi bundles
- Main plugin structure follows Eclipse PDE conventions
- Uses Java 11+ with extensive external libraries
- Integrates with ChatGPT API for AI-powered assistance
- Implements Quick Actions framework for ABAP development
- Supports multi-format document processing (PDF, DOC, TXT)
- Includes enterprise audit trail and version control

## Key Components

- Plugin manifest and OSGi bundle configuration
- Service layer for ChatGPT integration
- Quick Actions framework for one-click operations
- Document processors for various file formats
- UI components integrated with Eclipse workbench
- Audit system for enterprise tracking
- Security layer for API key management

## Development Guidelines

- Follow Eclipse plugin development patterns
- Use OSGi dependency injection where applicable
- Implement proper error handling and logging
- Ensure thread safety for UI operations
- Follow SAP ABAP development conventions
- Maintain backward compatibility with Eclipse 4.6+
