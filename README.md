Original App Design Project - README
===

# Food Friends

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This is an app that allows you to see your friends' favourite restaurants nearby you, and the favourite restaurants of the people you follow. You will be able to follow your friends on the app and see what restaurants they like, as well as see restaurants they want/plan to go to. The app will also ping the user when they a restaurant liked by their friends.


### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:**
    - Lifestyle, Social
- **Mobile:**
    - Uses Maps, Location, Real-Time, Push Notifications
- **Story:**
    - This is a dedicated platform where people can share the restaurants and food places that they love with each other.
- **Market:**
    - People that visit restaurants often/periodically
- **Habit:**
    - Wouldn't necessarily say addictive, but it is habit forming in the sense that when you are looking for places to eat, you can see places that your friends have been, and decide based on this. Can also open app to review a restaurant you've been to, which iis a habit aswell.
- **Scope:**
    - This app can be well defined and also accomplishable in the project scope timeline. might be a bit hard to implement all the features, but core features of the app can be implemented interestingly.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**
* User can register
* User can log in
* User can see list of most liked restaurants 
* User can search for someone to follow
* User can like a restaurant 
* User can search for a restaurant
* User can bookmark a restaurant
* When a restuarant address it clicked, should send us to google maps app
* User can see restaurant details and favourite count
* User can see bookmarked restaurants


**Optional Nice-to-have Stories**

* User can see map screen with pointers to nearby favourited restaurants
* User is suggested people to follow on entering app
* User can also see globally liked restaurants via toggle
* User can comment on a restaurant (pictures possible)
* User can see restaurant comments 
* User will receive push notifications when near liked restuarants


### 2. Screen Archetypes (Required)

* Registration Screen
    * User can register
* Login Screen
    * User can log in
* Restaurant Stream
    * User can see list of most liked restaurants 
    * User can like a restaurant 
    * User can bookmark a restaurant
* Restaurant Detail Screen
    * User can see restaurant details and favourite count
    * User can like a restaurant 
    * User can bookmark a restaurant
    * When a restuarant address it clicked, should send us to google maps app
* Search Stream
    * User can search for a restaurant
    * User can search for someone to follow
* Profile Stream
    * User can see bookmarked restaurants
    * See liked restaurants
    * Personal info 

OPTIONAL:
* Map Screen
    * User can see map screen with pointers to nearby favourited restaurants
* Onboard Screen
    * User is suggested people to follow on entering app
* Restaurant Stream (extra)
    * User can also see globally liked restaurants via toggle
* Restaurant Detail Screen
    * User can see restaurant comments 
* Comment Creation
    * User can comment on a restaurant (pictures possible)


### 3. Navigation (Required Features)

**Tab Navigation** (Tab to Screen)

* Explore Tab -> Restaurant Stream
* Search Tab -> Search Stream
* Profile Tab -> Profile Stream

**Flow Navigation** (Screen to Screen)

* Register Screen
   * Explore Tab (Restaurant Stream)
* Login Screen
   * Explore Tab (Restaurant Stream)
* Restaurant Stream
    * Restaurant Detail
* Search Stream
* Restaurant Detail
* Profile Stream
    * Restaurant Detail

OPTIONAL:

**Tab Navigation** (Tab to Screen)

* Map Tab ->Map Screen
* Explore Tab -> Restaurant Stream
* Search Tab -> Search Stream
* Profile Tab -> Profile Stream

**Flow Navigation** (Screen to Screen)

* Register Screen
   * Onboard Screen
* Onboard Screen
    * Explore Tab (Restaurant Stream)
* Login Screen
   * Explore Tab (Restaurant Stream)
* Restaurant Stream
    * Restaurant Detail
* Restaurant Detail Screen
    * Comment Creation
* Search Stream
    * Restaurant Detail
* Profile Stream
    * Restaurant Detail
* Map Screen
    * Restaurant Detail

##Technologies
* Yelp API
* Parse Database
* Google Maps API

## Wireframes
![IMG-3507](https://user-images.githubusercontent.com/51707797/173666231-92c69ecb-a4d8-4d0f-ac01-b251f67edb9b.jpg)
### USEFUL LINKS
https://www.google.com/search?q=database+first+android+studio&rlz=1C5GCEM_enUS1008US1008&oq=database+first+android&aqs=chrome.1.69i57j33i160l2.5701j0j1&sourceid=chrome&ie=UTF-8

## Schema 
 * Restaurant
 * Comment
 * User
 * Friends
 * UserLike
 * UserToGo


### Models
 * Restaurant
    * id (string)
    * name (string)
    * imageUrl (string)
    * city (string)
    * state (string)
    * zip code (number/string)
    * latitude (float)
    * longitude (float)
    * price (string)
    * address (string)

* User
    * id (string)
    * name (string)
    * username (string)
    * city (string)
    * state (string)
    * profilePhoto (ParseFile)

* UserLike
    * user (User Pointer)
    * restaurant (Restaurant Pointer)

* UserToGo
    * user (User Pointer)
    * restaurant (Restaurant Pointer)

* Friends
    * user1 (User Pointer)
    * user2 (User Pointer)

* Comment
    * user (User Pointer)
    * restaurant (Restaurant Pointer)
    * text (String)


[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]

- [OPTIONAL: List endpoints if using existing API such as Yelp]
 - Yelp API
     - GET https://api.yelp.com/v3/businesses/search 
 - Google Maps API
    - ImplicitIntent "http://maps.google.com/maps?saddr=long,lat&daddr=long,lat"
