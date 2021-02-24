FROM mcr.microsoft.com/dotnet/core/aspnet:3.1-alpine
WORKDIR /app
COPY ./menu-api-release ./

EXPOSE 80
CMD ["dotnet", "Menu.API.dll"]