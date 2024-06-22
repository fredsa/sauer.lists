package main

import (
	"context"
	"fmt"
	"log"

	firebase "firebase.google.com/go/v4"
	"firebase.google.com/go/v4/auth"
	"google.golang.org/api/iterator"
)

func main() {
	ctx := context.Background()
	app := initializeAppDefault(&firebase.Config{ProjectID: "sauer-lists"})

	client, err := app.Auth(ctx)
	if err != nil {
		log.Fatalf("error createing client: %v\n", err)
	}

	// bulkGetUsers(ctx, client)
	GetUsers(ctx, client)
}

func initializeAppDefault(config *firebase.Config) *firebase.App {
	app, err := firebase.NewApp(context.Background(), config)
	if err != nil {
		log.Fatalf("error initializing app: %v\n", err)
	}

	return app
}

func GetUsers(ctx context.Context, client *auth.Client) {
	iter := client.Users(ctx, "")
	for {
		u, err := iter.Next()
		if err == iterator.Done {
			break
		}
		if err != nil {
			log.Fatalf("error in iterator: %v\n", err)
		}
		fmt.Printf("%v %v\n", u.UID, u.Email)
		// client.DeleteUser(ctx, u.UID)
	}

}
