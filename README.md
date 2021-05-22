## sentry log retriever for slack

### Usage

create .env like the below
```
# mode
ENV_NAME="anything"

# sentry
SENTRY_AUTH_TOKEN="xxxxxxxxxxxxxxxxxxxxxxxxxx"
SENTRY_ORGANIZATION="xxxxx"
SENTRY_GROUPID="xxxxx"
SENTRY_RULE_URL="https://sentry.io/organizations/xxxxx/alerts/rules/yyyyy/nnnnn/"
SENTRY_RULE_NAME="Send a notification for new issues"

# slack
SLACK_TOKEN="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
SLACK_CHANNEL="channel_name"
SLACK_BOT_NAME="Sentry"
SLACK_ICON_URL="icon.png"
SLACK_FOOTER_ICON="footer.png"

# jdbc_url
JDBC_URL="jdbc:sqlite:sentry.db"
```

execute
```
sbt run
```
